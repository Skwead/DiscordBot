package br.com.finalelite.discord.bot.command.commands.moderation;

import br.com.finalelite.discord.bot.command.CommandPermission;
import br.com.finalelite.discord.bot.command.commands.moderation.utils.PunishmentType;
import br.com.finalelite.discord.bot.command.commands.moderation.utils.RevertPunishmentCommand;

public class UnWarnCommand extends RevertPunishmentCommand {
    public UnWarnCommand() {
        super(
                "unwarn",
                "remover uma repreensão feita a um usuário no Discord",
                CommandPermission.MODERATOR,
                PunishmentType.WARN
        );
    }
}
