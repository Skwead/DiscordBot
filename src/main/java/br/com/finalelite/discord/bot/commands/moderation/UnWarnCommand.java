package br.com.finalelite.discord.bot.commands.moderation;

import br.com.finalelite.discord.bot.entity.command.CommandPermission;
import br.com.finalelite.discord.bot.entity.command.RevertPunishmentCommandBase;
import br.com.finalelite.discord.bot.entity.punishment.PunishmentType;

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
