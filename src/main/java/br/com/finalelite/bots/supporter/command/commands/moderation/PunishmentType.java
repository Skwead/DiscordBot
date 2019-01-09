package br.com.finalelite.bots.supporter.command.commands.moderation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public enum PunishmentType {
    KICK,
    BAN,
    TEMP_BAN,
    MUTE,
    TEMP_MUTE,
    WARN,
    TEMP_WARN;

    public boolean isPermanent() {
        return !this.name().startsWith("TEMP_");
    }
}
