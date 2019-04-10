package br.com.finalelite.discord.bot.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DefaultCommandCategory {
    SUPPORT("Suporte", ":question:"),
    MODERATION("Moderação", "<:blobban:531459039998115840>"),
    SERVER("Servidor", "<:finalelite:531459244919226419>"),
    UTILS("Utilidades", ":flashlight:");

    private final String name;
    private final String emojiName;
}
