package br.com.finalelite.discord.bot.command.commands.moderation;

import br.com.finalelite.discord.bot.command.CommandPermission;
import br.com.finalelite.discord.bot.command.commands.moderation.utils.PunishmentType;
import br.com.finalelite.discord.bot.command.commands.moderation.utils.RevertPunishmentCommand;

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
