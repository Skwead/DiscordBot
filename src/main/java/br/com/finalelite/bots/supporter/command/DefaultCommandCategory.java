package br.com.finalelite.bots.supporter.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DefaultCommandCategory {
    SUPPORT(new CommandCategory("Suporte", ":question:")),
    MODERATION(new CommandCategory("Moderação", "<:blobban:531459039998115840>")),
    SERVER(new CommandCategory("Servidor", "<:finalelite:531459244919226419>")),
    UTILS(new CommandCategory("Utilidades", ":hammer_pick:"));

    private final CommandCategory category;

}
