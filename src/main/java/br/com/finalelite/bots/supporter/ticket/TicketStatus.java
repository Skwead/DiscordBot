package br.com.finalelite.bots.supporter.ticket;

public enum TicketStatus {
    CLOSED,
    OPENED,
    SPAM;

    public static TicketStatus getFromOrdinalId(byte id) {
        return TicketStatus.values()[id];
    }

}
