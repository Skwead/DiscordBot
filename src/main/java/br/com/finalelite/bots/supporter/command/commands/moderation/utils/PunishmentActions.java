package br.com.finalelite.bots.supporter.command.commands.moderation.utils;

import br.com.finalelite.bots.supporter.Supporter;

import java.util.function.Consumer;

public class PunishmentActions {
    public static final Consumer<Punishment> KICK = punishment -> punishment.getRelatedGuild().getController()
            .kick(punishment.getTarget(), punishment.getReason()).complete();

    public static final Consumer<Punishment> BAN = punishment -> punishment.getRelatedGuild().getController()
            .ban(punishment.getTarget(), 7, punishment.getReason()).complete();

    public static final Consumer<Punishment> UNBAN = punishment ->
        punishment.getRelatedGuild().getController()
                .unban(punishment.getTargetId()).complete();
    ;

    public static final Consumer<Punishment> MUTE = punishment -> punishment.getRelatedGuild().getController()
            .addRolesToMember(punishment.getTarget(), Supporter.getRoleById(Supporter.getInstance().getConfig().getMutedRoleId())).complete();

    public static final Consumer<Punishment> UNMUTE = punishment -> punishment.getRelatedGuild().getController()
            .removeRolesFromMember(punishment.getTarget(), Supporter.getRoleById(Supporter.getInstance().getConfig().getMutedRoleId())).complete();
}
