package br.com.finalelite.bots.supporter.command.commands.moderation;

import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.commands.moderation.utils.PunishmentCommand;
import br.com.finalelite.bots.supporter.command.commands.moderation.utils.PunishmentType;

public class MuteCommand extends PunishmentCommand {

    public MuteCommand() {
        super(
                "mute",
                "silencia um usuário do Discord",
                CommandPermission.MODERATOR,
                PunishmentType.MUTE
        );
    }

}
