package com.github.pauloo27.discord.bot.commands.moderation;

import com.github.pauloo27.discord.bot.Bot;
import com.github.pauloo27.discord.bot.entity.command.CommandPermission;
import com.github.pauloo27.discord.bot.entity.command.TempPunishmentCommandBase;
import com.github.pauloo27.discord.bot.entity.punishment.PunishmentType;
import com.github.pauloo27.discord.bot.utils.SimpleLogger;
import lombok.val;

public class TempMuteCommand extends TempPunishmentCommandBase {
    public TempMuteCommand() {
        super(
                "tempmute",
                "silencia temporáriamente um usuário do Discord",
                CommandPermission.SUPPORT,
                PunishmentType.TEMP_MUTE
        );

        new Thread(() -> {
            while (true) {

                SimpleLogger.log("Searching for muted users to pardon...");

                val instance = Bot.getInstance();
                val mutedRole = Bot.getRoleById(instance.getConfig().getMutedRoleId());
                instance.getJda().getGuilds().get(0)
                        .getMembersWithRoles(mutedRole)
                        .forEach(member -> {
                            val punishment = instance.getDatabase().getActivePunishmentByUser(member.getUser().getId(),
                                    PunishmentType.TEMP_MUTE, PunishmentType.MUTE);
                            if (punishment == null) {
                                SimpleLogger.log("Found invalid muted user %s#%s (s), unmuting...",
                                        member.getNickname() == null ? member.getEffectiveName() : member.getNickname(),
                                        member.getUser().getDiscriminator(), member.getUser().getId());
                                instance.getJda().getGuilds().get(0).getController().removeRolesFromMember(member, mutedRole).queue();
                            }
                        });

                SimpleLogger.log("Search ended.");
                try {
                    // 1 minute
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
