package com.github.pauloo27.discord.bot.commands.moderation;

import com.github.pauloo27.discord.bot.entity.command.CommandPermission;
import com.github.pauloo27.discord.bot.entity.command.EternalPunishmentCommandBase;
import com.github.pauloo27.discord.bot.entity.punishment.PunishmentType;

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
