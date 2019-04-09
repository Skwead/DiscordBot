package br.com.finalelite.bots.supporter.command.commands.moderation.utils;

import br.com.finalelite.bots.supporter.Supporter;
import br.com.finalelite.bots.supporter.utils.time.TimeUnits;
import lombok.val;

import java.util.Date;
import java.util.function.Consumer;

public class PunishmentActions {
    public static final Consumer<Punishment> KICK = punishment -> punishment.getRelatedGuild().getController()
            .kick(punishment.getTarget(), punishment.getReason()).complete();

    public static final Consumer<Punishment> BAN = punishment -> punishment.getRelatedGuild().getController()
            .ban(punishment.getTarget(), 7, punishment.getReason()).complete();

    public static final Consumer<Punishment> UNBAN = punishment ->
            punishment.getRelatedGuild().getController()
                    .unban(punishment.getTargetId()).complete();

    public static final Consumer<Punishment> WARN = punishment -> {
        val punishments = Supporter.getInstance().getDatabase().getActivePunishmentsByType(PunishmentType.WARN);
        if (punishments.size() == 0)
            return;

        if (punishments.size() == 1)
            ModerationUtils.apply(clonePunishment(
                    punishment,
                    PunishmentType.TEMP_MUTE,
                    punishments.size(),
                    TimeUnits.HOURS.addToDate(punishment.getDate(), 24)
            ).build());

        if (punishments.size() == 2)
            ModerationUtils.apply(clonePunishment(
                    punishment,
                    PunishmentType.KICK,
                    punishments.size(),
                    null
            ).build());

        if (punishments.size() == 3)
            ModerationUtils.apply(clonePunishment(
                    punishment,
                    PunishmentType.TEMP_BAN,
                    punishments.size(),
                    TimeUnits.HOURS.addToDate(punishment.getDate(), 24)
            ).build());

        if (punishments.size() == 4)
            ModerationUtils.apply(clonePunishment(
                    punishment,
                    PunishmentType.BAN,
                    punishments.size(),
                    null
            ).build());

    };

    public static final Consumer<Punishment> MUTE = punishment -> punishment.getRelatedGuild().getController()
            .addRolesToMember(punishment.getTarget(), Supporter.getRoleById(Supporter.getInstance().getConfig().getMutedRoleId())).complete();

    public static final Consumer<Punishment> UNMUTE = punishment -> punishment.getRelatedGuild().getController()
            .removeRolesFromMember(punishment.getTarget(), Supporter.getRoleById(Supporter.getInstance().getConfig().getMutedRoleId())).complete();

    private static Punishment.PunishmentBuilder clonePunishment(Punishment punishment, PunishmentType type, int punishmentCount, Date end) {
        val builder = Punishment.builder()
                .dateSeconds(punishment.getDateSeconds())
                .authorId(punishment.getAuthorId())
                .targetId(punishment.getTargetId())
                .relatedGuildId(punishment.getRelatedGuildId())
                .relatedChannelId(punishment.getRelatedChannelId())
                .relatedMessageId(punishment.getRelatedMessageId())
                .type(type)
                .reason(String.format("%d/5 avisos: %s", punishmentCount + 1, punishment.getReason()));

        if (end != null)
            builder.endSeconds(Punishment.parseDate(end));

        return builder;
    }
}
