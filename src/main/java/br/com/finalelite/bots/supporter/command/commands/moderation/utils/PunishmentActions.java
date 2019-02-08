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
            ModerationUtils.apply(Punishment.builder()
                    .date(punishment.getDate())
                    .author(punishment.getAuthor())
                    .target(punishment.getTarget())
                    .relatedGuild(punishment.getRelatedGuild())
                    .relatedChannel(punishment.getRelatedChannel())
                    .relatedMessage(punishment.getRelatedMessage())
                    .type(PunishmentType.TEMP_MUTE)
                    .reason("2/5 avisos: " + punishment.getReason())
                    .end(new Date(punishment.getDate().getTime() + (long) TimeUnits.HOURS.convert(24, TimeUnits.MILLISECONDS)))
                    .build());

        if (punishments.size() == 2)
            ModerationUtils.apply(Punishment.builder()
                    .date(punishment.getDate())
                    .author(punishment.getAuthor())
                    .target(punishment.getTarget())
                    .relatedGuild(punishment.getRelatedGuild())
                    .relatedChannel(punishment.getRelatedChannel())
                    .relatedMessage(punishment.getRelatedMessage())
                    .type(PunishmentType.KICK)
                    .reason("3/5 avisos: " + punishment.getReason())
                    .build());

        if (punishments.size() == 3)
            ModerationUtils.apply(Punishment.builder()
                    .date(punishment.getDate())
                    .author(punishment.getAuthor())
                    .target(punishment.getTarget())
                    .relatedGuild(punishment.getRelatedGuild())
                    .relatedChannel(punishment.getRelatedChannel())
                    .relatedMessage(punishment.getRelatedMessage())
                    .type(PunishmentType.TEMP_BAN)
                    .reason("4/5 avisos: " + punishment.getReason())
                    .end(new Date(punishment.getDate().getTime() + (long) TimeUnits.HOURS.convert(24, TimeUnits.MILLISECONDS)))
                    .build());

        if (punishments.size() == 4)
            ModerationUtils.apply(Punishment.builder()
                    .date(punishment.getDate())
                    .author(punishment.getAuthor())
                    .target(punishment.getTarget())
                    .relatedGuild(punishment.getRelatedGuild())
                    .relatedChannel(punishment.getRelatedChannel())
                    .relatedMessage(punishment.getRelatedMessage())
                    .type(PunishmentType.BAN)
                    .reason("5/5 avisos: " + punishment.getReason())
                    .build());
    };

    public static final Consumer<Punishment> MUTE = punishment -> punishment.getRelatedGuild().getController()
            .addRolesToMember(punishment.getTarget(), Supporter.getRoleById(Supporter.getInstance().getConfig().getMutedRoleId())).complete();

    public static final Consumer<Punishment> UNMUTE = punishment -> punishment.getRelatedGuild().getController()
            .removeRolesFromMember(punishment.getTarget(), Supporter.getRoleById(Supporter.getInstance().getConfig().getMutedRoleId())).complete();
}
