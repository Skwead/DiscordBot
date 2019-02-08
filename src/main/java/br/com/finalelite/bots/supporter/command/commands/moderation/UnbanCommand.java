package br.com.finalelite.bots.supporter.command.commands.moderation;

import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.commands.moderation.utils.PunishmentType;
import br.com.finalelite.bots.supporter.command.commands.moderation.utils.RevertPunishmentCommand;

public class UnbanCommand extends RevertPunishmentCommand {
    public UnbanCommand() {
        super(
                "unban",
                "desbane um usuário do Discord",
                CommandPermission.MAJOR_STAFF,
                PunishmentType.BAN, PunishmentType.TEMP_BAN
        );
    }
}
