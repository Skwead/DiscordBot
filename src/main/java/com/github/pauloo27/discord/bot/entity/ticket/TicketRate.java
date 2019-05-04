package com.github.pauloo27.discord.bot.entity.ticket;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum TicketRate {
    TERRIBLE("Péssimo", "\uD83D\uDE21"),
    BAD("Ruim", "\uD83D\uDE26"),
    REGULAR("Regular", "\uD83D\uDE10"),
    GOOD("Bom", "\uD83D\uDE42"),
    GREAT("Ótimo", "\uD83D\uDE00");

    @Getter
    private final String portugueseName;
    @Getter
    private final String emoji;

    public static TicketRate fromEmoji(String emoji) {
        return Arrays.stream(TicketRate.values())
                .filter(rate -> rate.getEmoji().equals(emoji))
                .findFirst().orElse(null);
    }
}
