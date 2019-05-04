package com.github.pauloo27.discord.bot.entity.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DefaultCommandCategory {
    SUPPORT("Suporte", ":question:"),
    MODERATION("Moderação", ":hammer_pick:"),
    UTILS("Utilidades", ":flashlight:");

    private final String name;
    private final String emojiName;
}
