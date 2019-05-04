package com.github.pauloo27.discord.bot.commands.moderation;

import com.github.pauloo27.discord.bot.entity.command.CommandPermission;
import com.github.pauloo27.discord.bot.entity.command.RevertPunishmentCommandBase;
import com.github.pauloo27.discord.bot.entity.punishment.PunishmentType;

public class UnBanCommand extends RevertPunishmentCommandBase {
    public UnBanCommand() {
        super(
                "unban",
                "desbane um usuário do Discord",
                CommandPermission.MODERATOR,
                PunishmentType.BAN, PunishmentType.TEMP_BAN
        );
    }
}
