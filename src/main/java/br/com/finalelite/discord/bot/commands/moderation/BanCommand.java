package br.com.finalelite.discord.bot.commands.moderation;

import br.com.finalelite.discord.bot.entity.command.CommandPermission;
import br.com.finalelite.discord.bot.entity.command.EternalPunishmentCommandBase;
import br.com.finalelite.discord.bot.entity.punishment.PunishmentType;

public class BanCommand extends EternalPunishmentCommandBase {

    public BanCommand() {
        super(
                "ban",
                "bane um usuário do Discord",
                CommandPermission.MODERATOR,
                PunishmentType.BAN
        );
    }
}
