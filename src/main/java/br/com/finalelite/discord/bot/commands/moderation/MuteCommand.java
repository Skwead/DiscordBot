package br.com.finalelite.discord.bot.commands.moderation;

import br.com.finalelite.discord.bot.entity.command.CommandPermission;
import br.com.finalelite.discord.bot.entity.command.EternalPunishmentCommandBase;
import br.com.finalelite.discord.bot.entity.punishment.PunishmentType;

public class MuteCommand extends EternalPunishmentCommandBase {

    public MuteCommand() {
        super(
                "mute",
                "silencia um usuário do Discord",
                CommandPermission.MODERATOR,
                PunishmentType.MUTE
        );
    }

}
