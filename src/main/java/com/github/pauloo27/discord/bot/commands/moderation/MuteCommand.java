package com.github.pauloo27.discord.bot.commands.moderation;

import com.github.pauloo27.discord.bot.entity.command.CommandPermission;
import com.github.pauloo27.discord.bot.entity.command.EternalPunishmentCommandBase;
import com.github.pauloo27.discord.bot.entity.punishment.PunishmentType;

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
