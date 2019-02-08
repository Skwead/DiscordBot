package br.com.finalelite.bots.supporter.command.commands.moderation;

import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.commands.moderation.utils.PunishmentType;
import br.com.finalelite.bots.supporter.command.commands.moderation.utils.RevertPunishmentCommand;

public class UnmuteCommand extends RevertPunishmentCommand {
    public UnmuteCommand() {
        super(
                "unmute",
                "desilencia um usuário do Discord",
                CommandPermission.MAJOR_STAFF,
                PunishmentType.MUTE, PunishmentType.TEMP_MUTE
        );
    }
}
