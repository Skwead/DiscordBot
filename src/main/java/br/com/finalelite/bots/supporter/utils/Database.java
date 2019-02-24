package br.com.finalelite.bots.supporter.utils;

import br.com.finalelite.bots.supporter.Supporter;
import br.com.finalelite.bots.supporter.command.commands.moderation.utils.Punishment;
import br.com.finalelite.bots.supporter.command.commands.moderation.utils.PunishmentType;
import br.com.finalelite.bots.supporter.ticket.Ticket;
import br.com.finalelite.bots.supporter.ticket.TicketStatus;
import br.com.finalelite.bots.supporter.vip.Invoice;
import br.com.finalelite.bots.supporter.vip.VIP;
import com.gitlab.pauloo27.core.sql.*;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class Database {

    private final String address;
    private final int port;
    private final String username;
    private final String password;
    private final String database;

    @Getter
    private EzSQL sql;
    private Table tickets;
    private Table captchas;
    private Table enabledVIPS;
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

        enabledVIPS = sql.createIfNotExists(VIP.class);

        sql.registerDataType(Invoice.class, DefaultDataTypes.BIGINT);
        sql.registerSerializer(Invoice.class,
                new DataSerializer<>(
                        Invoice::getId,
                        (invoiceClass, invoiceId) -> getInvoiceById((long) invoiceId)
                )
        );

        punishments = sql.createIfNotExists(Punishment.class);

        captchas = sql.createIfNotExists(Captcha.class);
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
        punishment.setReverted(true);

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
                .and().equals("reverted", false);
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

    // gets the invoices by an email
    public List<Invoice> getInvoicesByEmail(String email) {
        val connection = sql.getConnection();
        try (val st = connection.prepareStatement("SELECT id, user_id, price_id, paid FROM finalelite.invoices WHERE user_id  = (SELECT id from finalelite.users WHERE email = ?)")) {
            st.setString(1, email);
            val rs = st.executeQuery();
            val invoices = new ArrayList<Invoice>();
            while (rs.next()) {
                invoices.add(new Invoice(rs.getLong("id"), rs.getLong("user_id"), rs.getInt("price_id"), rs.getBoolean("paid")));
            }
            return invoices;
        } catch (SQLException e) {
            reconnectSQL(e);
            if (e.getMessage().startsWith("The last packet successfully received from the server was"))
                return getInvoicesByEmail(email);
        }
        return null;
    }

    // gets a invoice by its id
    public Invoice getInvoiceById(long id) {
        val connection = sql.getConnection();
        try (val st = connection.prepareStatement("SELECT id, user_id, price_id, paid FROM finalelite.invoices WHERE id  = ?")) {
            st.setLong(1, id);
            val rs = st.executeQuery();
            if (rs.next())
                return new Invoice(rs.getLong("id"), rs.getLong("user_id"), rs.getInt("price_id"), rs.getBoolean("paid"));
        } catch (SQLException e) {
            reconnectSQL(e);
            if (e.getMessage().startsWith("The last packet successfully received from the server was"))
                return getInvoiceById(id);
        }
        return null;
    }

    // set the invoice paid status to true (or 1)
    public void setInvoicePaid(long id) {
        val connection = sql.getConnection();
        try (val st = connection.prepareStatement("UPDATE finalelite.invoices SET paid = 1 WHERE id  = ?")) {
            st.setLong(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            reconnectSQL(e);
            if (e.getMessage().startsWith("The last packet successfully received from the server was"))
                setInvoicePaid(id);
        }
    }

    // changes the username used in the site
    public byte setUsername(long id, String name) {
        val connection = sql.getConnection();
        try (val st = connection.prepareStatement("UPDATE finalelite.users SET username = ? WHERE id  = ?")) {
            st.setString(1, name);
            st.setLong(2, id);
            st.executeUpdate();
            return 0;
        } catch (SQLException e) {
            if (e.getMessage().startsWith("Duplicate entry"))
                return 1;
            reconnectSQL(e);
            if (e.getMessage().startsWith("The last packet successfully received from the server was"))
                return setUsername(id, name);
        }
        return 2;
    }

    // gets the username
    public String getUsername(long id) {
        val connection = sql.getConnection();
        try (val st = connection.prepareStatement("SELECT username FROM finalelite.users WHERE id  = ?")) {
            st.setLong(1, id);
            val rs = st.executeQuery();
            if (rs.next())
                return rs.getString("username");
        } catch (SQLException e) {
            reconnectSQL(e);
            if (e.getMessage().startsWith("The last packet successfully received from the server was"))
                return getUsername(id);
        }
        return null;
    }

    // gets the discord used to active an invoice
    public String getDiscordIdByInvoiceId(long invoiceId) {
        val connection = sql.getConnection();
        try (val st = connection.prepareStatement("SELECT discordId FROM enabled_vips WHERE invoiceId  = ?")) {
            st.setLong(1, invoiceId);
            val rs = st.executeQuery();
            if (rs.next())
                return rs.getString("discordId");
        } catch (SQLException e) {
            reconnectSQL(e);
            if (e.getMessage().startsWith("The last packet successfully received from the server was"))
                return getDiscordIdByInvoiceId(invoiceId);
        }
        return null;
    }

    // registers a vip to a invoice (to avoid 2 people to use the same invoice)
    public byte registerVIP(VIP vip) {
        AtomicInteger status = new AtomicInteger();
        enabledVIPS.insert(vip).executeAndClose(e -> {
            // haha, someone trying to use the same invoice to get the VIP?
            if (e.getMessage().startsWith("Duplicate entry"))
                status.set(1);
            reconnectSQL((SQLException) e);
            if (e.getMessage().startsWith("The last packet successfully received from the server was"))
                status.set(2);
        });
        if (status.get() == 2)
            return registerVIP(vip);

        return (byte) status.get();
    }

    // gets the user id by the email used in the site
    public long getUserIdByEmail(String email) {
        try (val st = sql.getConnection().prepareStatement("SELECT id from finalelite.users WHERE email = ?")) {
            st.setString(1, email);
            val rs = st.executeQuery();
            if (rs.next())
                return rs.getLong("id");
        } catch (SQLException e) {
            reconnectSQL(e);
            if (e.getMessage().startsWith("The last packet successfully received from the server was"))
                return getUserIdByEmail(email);
        }
        return -1;
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
            Supporter.getInstance().shutdown("Cannot reconnect to SQL");
            System.exit(-3);
        }
    }
}
