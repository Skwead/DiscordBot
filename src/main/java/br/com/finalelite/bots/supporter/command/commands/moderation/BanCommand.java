package br.com.finalelite.bots.supporter.command.commands.moderation;

import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.commands.moderation.utils.PunishmentCommand;
import br.com.finalelite.bots.supporter.command.commands.moderation.utils.PunishmentType;

public class BanCommand extends PunishmentCommand {

    public BanCommand() {
        super(
                PunishmentType.BAN,
                "ban",
                "bane um usuário do Discord",
                CommandPermission.MAJOR_STAFF
        );
    }
}
