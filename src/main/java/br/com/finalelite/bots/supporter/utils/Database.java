package br.com.finalelite.bots.supporter.utils;

import br.com.finalelite.bots.supporter.ticket.Ticket;
import br.com.finalelite.bots.supporter.ticket.TicketStatus;
import br.com.finalelite.bots.supporter.vip.Invoice;
import br.com.finalelite.bots.supporter.vip.VIPRole;
import com.gitlab.pauloo27.core.sql.*;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.dv8tion.jda.core.entities.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class Database {

    private final String address;
    private final int port;
    private final String username;
    private final String password;
    private final String database;

    private EzSQL sql;
    private EzTable tickets;
    private EzTable enabledVIPS;

    public void connect() throws SQLException, ClassNotFoundException {
        sql = new EzSQL(EzSQLType.MYSQL)
                .withAddress(address, port)
                .withDefaultDatabase(database + "?autoReconnect=true")
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
    }

    public boolean canCreateTicket(String userId) throws SQLException {
        val rs = tickets.select(new EzSelect("userId")
                .where().equals("userId", userId)
                .and().equals("status", TicketStatus.SPAM.ordinal())).getResultSet();
        return !rs.next();
    }

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

    public Ticket getTicketByChannelId(String channelId) throws SQLException {
        val rs = tickets.select(new EzSelect("id, messageId, userId, subject, status")
                .where().equals("channelId", channelId)).getResultSet();
        if (!rs.next())
            return null;
        val ticket = new Ticket(rs.getInt("id"), rs.getString("userId"), rs.getString("messageId"), channelId, rs.getString("subject"), TicketStatus.getFromOrdinalId(rs.getByte("status")));
        rs.close();

        return ticket;
    }

    public void closeTicket(Ticket ticket) throws SQLException {
        tickets.update(new EzUpdate().set("status", TicketStatus.CLOSED.ordinal()).where().equals("id", ticket.getId())).close();
    }

    public void markTicketAsSpam(Ticket ticket) throws SQLException {
        tickets.update(new EzUpdate().set("status", TicketStatus.SPAM.ordinal()).where().equals("id", ticket.getId())).close();
    }

    public boolean hasOpenedTicket(String userId) throws SQLException {
        val rs = tickets.select(new EzSelect("userId")
                .where().equals("userId", userId)
                .and().equals("status", TicketStatus.OPENED.ordinal())).getResultSet();
        return !rs.next();
    }

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
            e.printStackTrace();
        }
        return null;
    }

    public Invoice getInvoiceById(long id) {
        val connection = sql.getConnection();
        try {
            val st = connection.prepareStatement("SELECT id, user_id, price_id, paid FROM finalelite.invoices WHERE id  = ?");
            st.setLong(1, id);
            val rs = st.executeQuery();
            if (rs.next())
                return new Invoice(rs.getLong("id"), rs.getLong("user_id"), rs.getInt("price_id"), rs.getBoolean("paid"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setInvoicePaid(long id) {
        val connection = sql.getConnection();
        try {
            val st = connection.prepareStatement("UPDATE finalelite.invoices SET paid = 1 WHERE id  = ?");
            st.setLong(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean registerVIP(String nickname, User discordUser, VIPRole role, String email, long userId, long paymentId) {
        try {
            enabledVIPS.insert(new EzInsert("nickname, discordId, userId, paymentId, email, vipRoleId",
                    nickname, discordUser.getId(), userId, paymentId, email, role.ordinal()));
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public long getUserIdByEmail(String email) {
        try {
            val st = sql.getConnection().prepareStatement("SELECT id from finalelite.users WHERE email = ?");
            st.setString(1, email);
            val rs = st.executeQuery();
            if (rs.next())
                return rs.getLong("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
