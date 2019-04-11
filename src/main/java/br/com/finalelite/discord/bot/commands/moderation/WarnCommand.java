package br.com.finalelite.discord.bot.commands.moderation;

import br.com.finalelite.discord.bot.entity.command.CommandPermission;
import br.com.finalelite.discord.bot.entity.command.EternalPunishmentCommandBase;
import br.com.finalelite.discord.bot.entity.punishment.PunishmentType;
import br.com.finalelite.discord.bot.utils.time.TimeUnits;
import net.dv8tion.jda.core.entities.Message;

import java.util.Date;

public class WarnCommand extends EternalPunishmentCommandBase {

    public WarnCommand() {
        super(
                "warn",
                "repreende um usuário no Discord",
                CommandPermission.MODERATOR,
                PunishmentType.WARN
        );
    }

    @Override
    public EndDateResult getEndDate(Date now, Message message, String[] args) {
        return new EndDateResult(true, new Date((now.getTime() + (long) TimeUnits.HOURS.convert(48, TimeUnits.MILLISECONDS))));
    }
}
