package br.com.finalelite.discord.bot.command.commands.moderation;

import br.com.finalelite.discord.bot.command.CommandPermission;
import br.com.finalelite.discord.bot.command.commands.moderation.utils.PunishmentCommand;
import br.com.finalelite.discord.bot.command.commands.moderation.utils.PunishmentType;

public class KickCommand extends PunishmentCommand {

    public KickCommand() {
        super(
                "kick",
                "expulsa um usuário do Discord",
                CommandPermission.MODERATOR,
                PunishmentType.KICK
        );
    }

}
