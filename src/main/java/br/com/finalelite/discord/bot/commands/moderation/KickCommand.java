package br.com.finalelite.discord.bot.commands.moderation;

import br.com.finalelite.discord.bot.entity.command.CommandPermission;
import br.com.finalelite.discord.bot.entity.command.EternalPunishmentCommandBase;
import br.com.finalelite.discord.bot.entity.punishment.PunishmentType;

public class KickCommand extends EternalPunishmentCommandBase {

    public KickCommand() {
        super(
                "kick",
                "expulsa um usuário do Discord",
                CommandPermission.MODERATOR,
                PunishmentType.KICK
        );
    }

}
