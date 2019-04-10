package br.com.finalelite.discord.bot.commands.moderation;

import br.com.finalelite.discord.bot.entity.command.CommandPermission;
import br.com.finalelite.discord.bot.entity.command.PunishmentCommand;
import br.com.finalelite.discord.bot.entity.punishment.PunishmentType;
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
