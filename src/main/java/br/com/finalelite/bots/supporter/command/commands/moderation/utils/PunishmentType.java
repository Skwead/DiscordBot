package br.com.finalelite.bots.supporter.command.commands.moderation.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Getter
public enum PunishmentType {
    KICK(PunishmentActions.KICK, null),
    BAN(PunishmentActions.BAN, PunishmentActions.UNBAN),
    TEMP_BAN(PunishmentActions.KICK, null),
    MUTE(PunishmentActions.MUTE, PunishmentActions.UNMUTE),
    TEMP_MUTE(PunishmentActions.MUTE, PunishmentActions.UNMUTE),
    WARN(null, null);

    private final Consumer<Punishment> apply;
    private final Consumer<Punishment> remove;

    public static PunishmentType fromOrdinal(int ordinal) {
        if (PunishmentType.values().length < ordinal)
            return null;
        return PunishmentType.values()[ordinal];
    }

    public void apply(Punishment punishment) {
        if (apply != null)
            apply.accept(punishment);
    }

    public void remove(Punishment punishment) {
        if (remove != null)
            remove.accept(punishment);
    }


    public boolean isPermanent() {
        return !this.name().startsWith("TEMP_");
    }

}
