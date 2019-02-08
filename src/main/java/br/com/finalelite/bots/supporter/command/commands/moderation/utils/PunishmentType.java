package br.com.finalelite.bots.supporter.command.commands.moderation.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Getter
public enum PunishmentType {
    KICK("Expulsão", PunishmentActions.KICK, null),
    BAN("Banimento", PunishmentActions.BAN, PunishmentActions.UNBAN),
    TEMP_BAN("Banimento temporário", PunishmentActions.KICK, null),
    MUTE("Silenciação", PunishmentActions.MUTE, PunishmentActions.UNMUTE),
    TEMP_MUTE("Silenciação temporária", PunishmentActions.MUTE, PunishmentActions.UNMUTE),
    WARN("Aviso", PunishmentActions.WARN, null);

    private final String displayName;
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

    public void revert(Punishment punishment) {
        if (remove != null)
            remove.accept(punishment);
    }


    public boolean isPermanent() {
        return !this.name().startsWith("TEMP_");
    }

}
