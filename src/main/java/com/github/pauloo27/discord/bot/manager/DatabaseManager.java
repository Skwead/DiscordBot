package com.github.pauloo27.discord.bot.manager;

import com.github.pauloo27.discord.bot.Bot;
import com.github.pauloo27.discord.bot.entity.Captcha;
import com.github.pauloo27.discord.bot.entity.punishment.Punishment;
import com.github.pauloo27.discord.bot.entity.punishment.PunishmentType;
import com.github.pauloo27.discord.bot.entity.ticket.Ticket;
import com.github.pauloo27.discord.bot.entity.ticket.TicketRate;
import com.github.pauloo27.discord.bot.entity.ticket.TicketStatus;
import com.github.pauloo27.discord.bot.utils.SimpleLogger;
import com.gitlab.pauloo27.core.sql.EzMySQL;
import com.gitlab.pauloo27.core.sql.EzSQL;
import com.gitlab.pauloo27.core.sql.Select;
import com.gitlab.pauloo27.core.sql.Table;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class DatabaseManager {

    private final String address;
    private final int port;
    private final String username;
    private final String password;
    private final String database;

    @Getter
    private EzSQL sql;
    private Table tickets;
    private Table captchas;
    private Table punishments;

    private static void handleException(Exception e) {
        SimpleLogger.sendStackTraceToOwner(e);
    }

    public void connect() throws SQLException, ClassNotFoundException {
        sql = new EzMySQL()
                .withAddress(address, port)
                .withDefaultDatabase(database)
                .withLogin(username, password);

        sql.registerDriver().connect();

        tickets = sql.createIfNotExists(Ticket.class);

        punishments = sql.createIfNotExists(Punishment.class);

        captchas = sql.createIfNotExists(Captcha.class);

        // keep the connection alive
        new Thread(() -> {
            try {
                Thread.sleep(60 * 60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                punishments.select("id").where().equals("id", 1).limit(1).execute().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();

    }

    public void createCaptcha(String userId, String channelId) {
        captchas.insert(Captcha.builder().userId(userId).channelId(channelId).build()).executeAndClose();
    }

    public String getCaptchaUserIdByChannelId(String channelId) {
        return captchas.select()
                .where().equals("channelId", channelId).execute().to(Captcha.class).getUserId();
    }

    public Punishment getActivePunishmentByUser(String targetId, PunishmentType... types) {
        checkTypes(types);

        val select = preparePunishmentSelectQuery(types);
        select.and().equals("targetId", targetId).limit(1);

        return select.execute().to(Punishment.class);
    }

    public List<Punishment> getActivePunishmentsByUser(String targetId, PunishmentType... types) {
        checkTypes(types);

        return preparePunishmentSelectQuery(types)
                .and().equals("targetId", targetId).execute()
                .toList(Punishment.class);
    }

    public String getCaptchaChannelIdByUserId(String userId) {
        val captcha = captchas.select()
                .where().equals("userId", userId)
                .limit(1)
                .and().equals("status", Captcha.Status.WAITING.name()).execute().to(Captcha.class);

        if (captcha == null)
            return null;

        return captcha.getChannelId();
    }

    public void addPunishment(Punishment punishment) {
        punishments.insert(punishment).executeAndClose();
    }

    public boolean revertPunishment(Punishment punishment) {
        return punishments.update(punishment).execute().getUpdatedRows() != 0;
    }

    public Punishment getPunishmentById(int id) {
        return punishments.select().where().equals("id", id).execute().to(Punishment.class);
    }

    public List<Punishment> getActivePunishmentsByType(PunishmentType... types) {
        checkTypes(types);

        return preparePunishmentSelectQuery(types).execute().toList(Punishment.class);
    }

    private void checkTypes(PunishmentType[] types) {
        Preconditions.checkNotNull(types);
        Preconditions.checkState(types.length != 0, "Types cannot be empty");
    }

    private Select preparePunishmentSelectQuery(PunishmentType[] types) {
        val select = punishments.select();

        select.where()
                .openParentheses()
                .equals("end", -1)
                .or()
                .moreThan("end", new Date().getTime() / 1000)
                .closeParentheses()
                .and().isNull("revertedById");
        val where = select.and().openParentheses();
        IntStream.range(0, types.length).forEach(index -> {
            val type = types[index];

            where.equals("type", type.name());

            if (index != types.length - 1)
                select.or();
        });
        select.closeParentheses();
        return select;
    }

    public void setCaptchaStatus(String channelId, Captcha.Status status) {
        captchas.update().set("status", status.name()).where().equals("channelId", channelId).executeAndClose();
    }

    public void removeCaptchaByChannelId(String channelId) {
        captchas.delete().where().equals("channelId", channelId).executeAndClose();
    }

    public Ticket rateTicketByMessageId(TicketRate rate, String messageId) {
        tickets.update()
                .set("rate", rate.name())
                .where()
                .equals("rateMessageId", messageId)
                .executeAndClose();

        return tickets.select().where().equals("rateMessageId", messageId).execute().to(Ticket.class);
    }

    public void deleteTicket(Ticket ticket, String logMessageId, String rateMessageId) {
        ticket.setStatus(TicketStatus.DELETED);
        ticket.setLogMessageId(logMessageId);
        ticket.setRateMessageId(rateMessageId);
        tickets.update(ticket).executeAndClose();
    }

    // checks if the user has committed spam
    public boolean canCreateTicket(String userId) {
        try (val rs = tickets.select()
                .where().equals("userId", userId)
                .and().equals("status", TicketStatus.SPAM.name()).execute().getResultSet()) {
            return !rs.next();
        } catch (SQLException e) {
            reconnectSQL(e);
            if (e.getMessage().startsWith("The last packet successfully received from the server was"))
                return canCreateTicket(userId);
        }
        return false;
    }

    // creates a ticket and return a instance
    public Ticket createReturningTicket(Ticket ticket) {
        try {
            tickets.insert(ticket).executeAndClose();

            val rs = tickets.select("id")
                    .where().equals("status", TicketStatus.OPENED.name())
                    .and().equals("userId", ticket.getUserId())
                    .limit(1).execute().getResultSet();
            if (!rs.next())
                return null;
            int id = rs.getInt("id");
            rs.close();
            ticket.setId(id);
            return ticket;
        } catch (SQLException e) {
            reconnectSQL(e);
            if (e.getMessage().startsWith("The last packet successfully received from the server was"))
                return createReturningTicket(ticket);
        }
        return null;
    }

    // gets the ticket by the channel id
    public Ticket getTicketByChannelId(String channelId) {
        return tickets.select().where().equals("channelId", channelId).execute().to(Ticket.class);
    }

    // closes a ticket
    public void closeTicket(Ticket ticket) {
        ticket.setStatus(TicketStatus.CLOSED);
        ticket.setCloseDate((int) (new Date().getTime() / 1000));
        tickets.update(ticket).executeAndClose();
    }

    // marks ticket as spam
    public void markTicketAsSpam(Ticket ticket) {
        ticket.setStatus(TicketStatus.SPAM);

        tickets.update(ticket);
    }

    // checks if the user has an opened ticket
    public boolean hasOpenedTicket(String userId) {
        try (val rs = tickets.select("userId")
                .where().equals("userId", userId)
                .and().equals("status", TicketStatus.OPENED.name()).execute().getResultSet()) {
            return !rs.next();
        } catch (SQLException e) {
            reconnectSQL(e);
            if (e.getMessage().startsWith("The last packet successfully received from the server was"))
                return hasOpenedTicket(userId);
        }
        return false;
    }

    // try to reconnect to the SQL because sometimes we get timed out :(
    public void reconnectSQL(SQLException e) {
        e.printStackTrace();
        handleException(e);
        try {
            sql.connect();
        } catch (SQLException e1) {
            e1.printStackTrace();
            handleException(e1);
            // oh boy, we're fired, lets just stop the bot
            Bot.getInstance().shutdown("Cannot reconnect to SQL");
            System.exit(-3);
        }
    }

    public void updateTicket(Ticket ticket) {
        tickets.update(ticket).executeAndClose();
    }
}
