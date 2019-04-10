package br.com.finalelite.discord.bot.command.commands.moderation;

import br.com.finalelite.discord.bot.command.CommandPermission;
import br.com.finalelite.discord.bot.command.commands.moderation.utils.PunishmentCommand;
import br.com.finalelite.discord.bot.command.commands.moderation.utils.PunishmentType;

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
