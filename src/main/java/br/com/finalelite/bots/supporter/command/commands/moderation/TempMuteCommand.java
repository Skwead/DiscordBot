package br.com.finalelite.bots.supporter.command.commands.moderation;

import br.com.finalelite.bots.supporter.Supporter;
import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.commands.moderation.utils.PunishmentType;
import br.com.finalelite.bots.supporter.command.commands.moderation.utils.TempPunishmentCommand;
import br.com.finalelite.bots.supporter.utils.SimpleLogger;
import lombok.val;

public class TempMuteCommand extends TempPunishmentCommand {
    public TempMuteCommand() {
        super(
                PunishmentType.TEMP_MUTE,
                "tempmute",
                "silencia temporáriamente um usuário do Discord",
                CommandPermission.STAFF
        );

        new Thread(() -> {
            while (true) {

                SimpleLogger.log("Searching for muted users to pardon...");

                val instance = Supporter.getInstance();
                val mutedRole = Supporter.getRoleById(instance.getConfig().getMutedRoleId());
                instance.getJda().getGuilds().get(0)
                        .getMembersWithRoles(mutedRole)
                        .forEach(member -> {
                            val punishment = instance.getDatabase().getActivePunishmentByUser(member.getUser().getId(),
                                    PunishmentType.TEMP_MUTE, PunishmentType.BAN);
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
