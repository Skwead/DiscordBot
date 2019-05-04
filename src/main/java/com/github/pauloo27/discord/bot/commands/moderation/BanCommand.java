package com.github.pauloo27.discord.bot.commands.moderation;

import com.github.pauloo27.discord.bot.entity.command.CommandPermission;
import com.github.pauloo27.discord.bot.entity.command.EternalPunishmentCommandBase;
import com.github.pauloo27.discord.bot.entity.punishment.PunishmentType;

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
