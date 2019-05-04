package com.github.pauloo27.discord.bot.commands.moderation;

import com.github.pauloo27.discord.bot.entity.command.CommandPermission;
import com.github.pauloo27.discord.bot.entity.command.RevertPunishmentCommandBase;
import com.github.pauloo27.discord.bot.entity.punishment.PunishmentType;

public class UnMuteCommand extends RevertPunishmentCommandBase {
    public UnMuteCommand() {
        super(
                "unmute",
                "desilencia um usuário do Discord",
                CommandPermission.MODERATOR,
                PunishmentType.MUTE, PunishmentType.TEMP_MUTE
        );
    }
}
