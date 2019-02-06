package br.com.finalelite.bots.supporter.command.commands.moderation;

import br.com.finalelite.bots.supporter.Supporter;
import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.commands.moderation.utils.PunishmentType;
import br.com.finalelite.bots.supporter.command.commands.moderation.utils.TempPunishmentCommand;
import br.com.finalelite.bots.supporter.utils.SimpleLogger;
import lombok.val;

public class TempBanCommand extends TempPunishmentCommand {

    public TempBanCommand() {
        super(
                PunishmentType.TEMP_BAN,
                "tempban",
                "bane um usuário temporáriamente do Discord",
                CommandPermission.MAJOR_STAFF
        );

        val jda = Supporter.getInstance().getJda();

        SimpleLogger.log("Searching for banned users.");
        Supporter.getInstance().getDatabase().getActivePunishmentsByType(PunishmentType.TEMP_BAN).stream()
                .filter(punishment -> punishment.getTarget() != null)
                .forEach(punishment -> {
                    SimpleLogger.log("Found banned user %s, kicking...", punishment.getTarget().getUser().getId());
                    jda.getGuilds().get(0).getController().kick(punishment.getTarget(), punishment.getReason()).complete();
                });
        SimpleLogger.log("Search ended.");
    }

}
