package br.com.finalelite.discord.bot.commands.moderation;

import br.com.finalelite.discord.bot.entity.command.CommandPermission;
import br.com.finalelite.discord.bot.entity.punishment.PunishmentType;
import br.com.finalelite.discord.bot.entity.command.RevertPunishmentCommandBase;

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
