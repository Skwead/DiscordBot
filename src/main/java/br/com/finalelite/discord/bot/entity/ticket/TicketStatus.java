package br.com.finalelite.discord.bot.entity.ticket;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum TicketStatus {
    CLOSED("\uD83D\uDC97"),
    OPENED("\uD83D\uDC9A"),
    SPAM("\uD83D\uDDA4"),
    DELETED("");

    private final String emoji;

    public static TicketStatus getFromOrdinalId(byte id) {
        return TicketStatus.values()[id];
    }

    public static TicketStatus getByEmoji(String emoji) {
        return Arrays.stream(TicketStatus.values())
                .filter(status -> status.getEmoji().equals(emoji))
                .findFirst()
                .orElse(null);
    }

}
