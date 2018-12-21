package br.com.finalelite.bots.supporter.utils;

import br.com.finalelite.bots.supporter.Main;
import br.com.finalelite.bots.supporter.ticket.Ticket;
import br.com.finalelite.bots.supporter.ticket.TicketStatus;
import br.com.finalelite.bots.supporter.vip.Invoice;
import com.gitlab.pauloo27.core.sql.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.dv8tion.jda.core.MessageBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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
    private EzTable tickets;
    private EzTable enabledVIPS;

    public void connect() throws SQLException, ClassNotFoundException {
        sql = new EzSQL(EzSQLType.MYSQL)
                .withAddress(address, port)
                // look, a gambiarra
                .withDefaultDatabase(database)
                .withLogin(username, password);

        sql.registerDriver().connect();

        tickets = sql.createIfNotExists(
                new EzTableBuilder("discord_tickets")
                        .withColumn(new EzColumnBuilder("id", EzDataType.PRIMARY_KEY))
                        .withColumn(new EzColumnBuilder("userId", EzDataType.VARCHAR, 64))
                        .withColumn(new EzColumnBuilder("messageId", EzDataType.VARCHAR, 64))
                        .withColumn(new EzColumnBuilder("channelId", EzDataType.VARCHAR, 64))
                        .withColumn(new EzColumnBuilder("subject", EzDataType.VARCHAR, 500))
                        .withColumn(new EzColumnBuilder("status", EzDataType.TINYINT))
        );

        enabledVIPS = sql.createIfNotExists(
                new EzTableBuilder("enabled_vips")
                        .withColumn(new EzColumnBuilder("invoiceId", EzDataType.BIGINT, EzAttribute.UNIQUE))
                        .withColumn(new EzColumnBuilder("discordId", EzDataType.VARCHAR, 64, EzAttribute.UNIQUE)));


        // to avoid timeout, lets run a simple query every hour
        new Thread(() -> {
            try {
                tickets.select(new EzSelect("id").where().equals("id", 1)).close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000 * 60 * 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // checks if the user has commited spam
    public boolean canCreateTicket(String userId) throws SQLException {
        val rs = tickets.select(new EzSelect("userId")
                .where().equals("userId", userId)
                .and().equals("status", TicketStatus.SPAM.ordinal())).getResultSet();
        return !rs.next();
    }

    // creates a ticket and return a instance
    public Ticket createReturningTicket(String userId, String messageId, String subject, String channelId) throws SQLException {
        tickets.insert(new EzInsert("userId, subject, channelId, status, messageId", userId, subject, channelId, TicketStatus.OPENED.ordinal(), messageId)).close();
        val rs = tickets.select(new EzSelect("id")
                .where().equals("channelId", channelId)
                .limit(1)).getResultSet();
        if (!rs.next())
            return null;
        int id = rs.getInt("id");
        rs.close();
        return new Ticket(id, userId, messageId, channelId, subject, TicketStatus.OPENED);
    }

    // gets the ticket by the channel id
    public Ticket getTicketByChannelId(String channelId) throws SQLException {
        val rs = tickets.select(new EzSelect("id, messageId, userId, subject, status")
                .where().equals("channelId", channelId)).getResultSet();
        if (!rs.next())
            return null;
        val ticket = new Ticket(rs.getInt("id"), rs.getString("userId"), rs.getString("messageId"), channelId, rs.getString("subject"), TicketStatus.getFromOrdinalId(rs.getByte("status")));
        rs.close();

        return ticket;
    }

    // closes a ticket
    public void closeTicket(Ticket ticket) throws SQLException {
        tickets.update(new EzUpdate().set("status", TicketStatus.CLOSED.ordinal()).where().equals("id", ticket.getId())).close();
    }

    // marks ticket as spam
    public void markTicketAsSpam(Ticket ticket) throws SQLException {
        tickets.update(new EzUpdate().set("status", TicketStatus.SPAM.ordinal()).where().equals("id", ticket.getId())).close();
    }

    // checks if the user has an opened ticket
    public boolean hasOpenedTicket(String userId) throws SQLException {
        val rs = tickets.select(new EzSelect("userId")
                .where().equals("userId", userId)
                .and().equals("status", TicketStatus.OPENED.ordinal())).getResultSet();
        return !rs.next();
    }

    // gets the invoices by an email
    public List<Invoice> getInvoicesByEmail(String email) {
        val connection = sql.getConnection();
        try {
            val st = connection.prepareStatement("SELECT id, user_id, price_id, paid FROM finalelite.invoices WHERE user_id  = (SELECT id from finalelite.users WHERE email = ?)");
            st.setString(1, email);
            val rs = st.executeQuery();
            val invoices = new ArrayList<Invoice>();
            while (rs.next()) {
                invoices.add(new Invoice(rs.getLong("id"), rs.getLong("user_id"), rs.getInt("price_id"), rs.getBoolean("paid")));
            }
            return invoices;
        } catch (SQLException e) {
            reconnectSQL(e);
        }
        return null;
    }

    // gets a invoice by its id
    public Invoice getInvoiceById(long id) {
        val connection = sql.getConnection();
        try {
            val st = connection.prepareStatement("SELECT id, user_id, price_id, paid FROM finalelite.invoices WHERE id  = ?");
            st.setLong(1, id);
            val rs = st.executeQuery();
            if (rs.next())
                return new Invoice(rs.getLong("id"), rs.getLong("user_id"), rs.getInt("price_id"), rs.getBoolean("paid"));
        } catch (SQLException e) {
            reconnectSQL(e);
        }
        return null;
    }

    // set the invoice paid status to true (or 1)
    public void setInvoicePaid(long id) {
        val connection = sql.getConnection();
        try {
            val st = connection.prepareStatement("UPDATE finalelite.invoices SET paid = 1 WHERE id  = ?");
            st.setLong(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            reconnectSQL(e);
        }
    }

    // changes the username used in the site
    public byte setUsername(long id, String name) {
        val connection = sql.getConnection();
        try {
            val st = connection.prepareStatement("UPDATE finalelite.users SET username = ? WHERE id  = ?");
            st.setString(1, name);
            st.setLong(2, id);
            st.executeUpdate();
            return 0;
        } catch (SQLException e) {
            if (e.getMessage().startsWith("Duplicate entry"))
                return 1;
            reconnectSQL(e);
        }
        return 2;
    }

    // gets the username
    public String getUsername(long id) {
        val connection = sql.getConnection();
        try {
            val st = connection.prepareStatement("SELECT username FROM finalelite.users WHERE id  = ?");
            st.setLong(1, id);
            val rs = st.executeQuery();
            if (rs.next())
                return rs.getString("username");
        } catch (SQLException e) {
            reconnectSQL(e);
        }
        return null;
    }

    // gets the discord used to active an invoice
    public String getDiscordIdByInvoiceId(long invoiceId) {
        val connection = sql.getConnection();
        try {
            val st = connection.prepareStatement("SELECT discordId FROM enabled_vips WHERE invoiceId  = ?");
            st.setLong(1, invoiceId);
            val rs = st.executeQuery();
            if (rs.next())
                return rs.getString("discordId");
        } catch (SQLException e) {
            reconnectSQL(e);
        }
        return null;
    }


    // registers a vip to a invoice (to avoid 2 people to use the same invoice)
    public byte registerVIP(String discordId, long invoiceId) {
        try {
            enabledVIPS.insert(new EzInsert("discordId, invoiceId", discordId, invoiceId));
            return 0;
        } catch (SQLException e) {
            // haha, someone trying to use the same invoice to get the VIP?
            if (e.getMessage().startsWith("Duplicate entry"))
                return 1;
            reconnectSQL(e);
        }
        return 2;
    }

    // gets the user if by the email used in the site
    public long getUserIdByEmail(String email) {
        try {
            val st = sql.getConnection().prepareStatement("SELECT id from finalelite.users WHERE email = ?");
            st.setString(1, email);
            val rs = st.executeQuery();
            if (rs.next())
                return rs.getLong("id");
        } catch (SQLException e) {
            reconnectSQL(e);
        }
        return -1;
    }

    // handle a exception (send to the bot owner) (i don't know its in this class)
    public static void handleException(Throwable e) {
        val pv = Main.getJda().getUserById(Main.getConfig().getOwnerId()).openPrivateChannel().complete();
        val sb = new StringBuilder();
        sb.append("**Look, a poem:**\n");
        sb.append(e.getMessage()).append("\n");
        sb.append(e.getCause()).append("\n");
        Arrays.stream(e.getStackTrace()).forEach(stackTraceElement -> sb.append(stackTraceElement.toString()).append("\n"));
        val lines = Arrays.asList(sb.toString().split("\n"));
        val times = lines.size() / 10;
        IntStream.range(0, times == 0 ? 1 : times).forEach(time -> {
            val tempLines = lines.stream().skip(time * 10).limit(10).collect(Collectors.toCollection(ArrayList::new));
            tempLines.add(time == 0 ? 1 : 0, "```java");
            tempLines.add("```");
            pv.sendMessage(new MessageBuilder(String.join("\n", tempLines)).build()).complete();
        });
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
            Main.shutdown("Cannot reconnect to SQL");
            System.exit(-3);
        }
    }
}
