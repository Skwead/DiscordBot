package br.com.finalelite.discord.bot.command.commands.moderation;

import br.com.finalelite.discord.bot.command.CommandPermission;
import br.com.finalelite.discord.bot.command.commands.moderation.utils.PunishmentCommand;
import br.com.finalelite.discord.bot.command.commands.moderation.utils.PunishmentType;
import br.com.finalelite.discord.bot.utils.time.TimeUnits;

import java.util.Date;

public class WarnCommand extends PunishmentCommand {

    public WarnCommand() {
        super(
                "warn",
                "repreende um usuário no Discord",
                CommandPermission.MODERATOR,
                PunishmentType.WARN
        );
    }

    @Override
    public Date getDefaultEndDate(Date now) {
        return new Date((now.getTime() + (long) TimeUnits.HOURS.convert(48, TimeUnits.MILLISECONDS)));
    }
}
