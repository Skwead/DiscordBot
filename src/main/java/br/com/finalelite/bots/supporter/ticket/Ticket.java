package br.com.finalelite.bots.supporter.ticket;

import lombok.Data;

@Data
public class Ticket {
    private final int id;
    private final String userId;
    private final String messageId;
    private final String channelId;
    private final String subject;
    private final TicketStatus status;
}


