package br.com.finalelite.bots.supporter.command.commands.moderation;

import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.commands.moderation.utils.PunishmentType;
import br.com.finalelite.bots.supporter.command.commands.moderation.utils.RevertPunishmentCommand;

public class UnMuteCommand extends RevertPunishmentCommand {
    public UnMuteCommand() {
        super(
                "unmute",
                "desilencia um usuário do Discord",
                CommandPermission.MODERATOR,
                PunishmentType.MUTE, PunishmentType.TEMP_MUTE
        );
    }
}
