package br.com.finalelite.bots.supporter.utils;

import br.com.finalelite.bots.supporter.ticket.Ticket;
import br.com.finalelite.bots.supporter.ticket.TicketStatus;
import com.gitlab.pauloo27.core.sql.*;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.sql.SQLException;

@RequiredArgsConstructor
public class Database {

    private final String address;
    private final int port;
    private final String username;
    private final String password;
    private final String database;

    private EzSQL sql;
    private EzTable tickets;


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
}
