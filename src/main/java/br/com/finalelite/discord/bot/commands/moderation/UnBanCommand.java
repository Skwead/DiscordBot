package br.com.finalelite.discord.bot.commands.moderation;

import br.com.finalelite.discord.bot.entity.command.CommandPermission;
import br.com.finalelite.discord.bot.entity.punishment.PunishmentType;
import br.com.finalelite.discord.bot.entity.command.RevertPunishmentCommand;

public class UnBanCommand extends RevertPunishmentCommand {
    public UnBanCommand() {
        super(
                "unban",
                "desbane um usuário do Discord",
                CommandPermission.MODERATOR,
                PunishmentType.BAN, PunishmentType.TEMP_BAN
        );
    }
}
