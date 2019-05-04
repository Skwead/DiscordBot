package com.github.pauloo27.discord.bot.commands.moderation;

import com.github.pauloo27.discord.bot.entity.command.CommandPermission;
import com.github.pauloo27.discord.bot.entity.command.RevertPunishmentCommandBase;
import com.github.pauloo27.discord.bot.entity.punishment.PunishmentType;

public class UnWarnCommand extends RevertPunishmentCommandBase {
    public UnWarnCommand() {
        super(
                "unwarn",
                "remover uma repreensão feita a um usuário no Discord",
                CommandPermission.MODERATOR,
                PunishmentType.WARN
        );
    }
}
