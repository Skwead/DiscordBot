package com.github.pauloo27.discord.bot.entity.command;

import com.github.pauloo27.discord.bot.entity.punishment.PunishmentType;
import com.github.pauloo27.discord.bot.utils.time.TimeUnits;
import lombok.val;
import lombok.var;
import net.dv8tion.jda.core.entities.Message;

import java.util.Date;

public class TempPunishmentCommandBase extends PunishmentCommandBase {
    public TempPunishmentCommandBase(String name, String description, CommandPermission permission, PunishmentType type) {
        super(name, description, permission, type);
    }

    @Override
    protected String getErrorMessage() {
        return "use `" + getDisplayName() + " <usuário> <tempo> <unidade de tempo> [<motivo>]`.";
    }

    @Override
    protected boolean isArgumentsValid(Message message, String[] args) {
        val guild = message.getGuild();
        return message.getMentionedUsers().size() < 1 || args.length < 3 || !args[0].equals(guild.getMember(message.getMentionedUsers().get(0)).getAsMention());
    }

    @Override
    public EndDateResult getEndDate(Date now, Message message, String[] args) {
        val end = (Date) now.clone();
        var endsWithComma = false;
        var argumentIndex = 1;
        do {
            var time = 0;
            try {
                time = Integer.parseInt(args[argumentIndex]);
            } catch (NumberFormatException e) {
                return new EndDateResult(false, null);
            }

            val rawArgument = args[argumentIndex + 1];

            endsWithComma = rawArgument.endsWith(",");

            val rawTimeUnit = endsWithComma ? rawArgument.substring(0, rawArgument.length() - 1) : rawArgument;

            val timeUnit = TimeUnits.getUnitByName(rawTimeUnit);
            if (timeUnit == null) {
                return new EndDateResult(false, null);
            }

            end.setTime((long) (end.getTime() + timeUnit.convert(time, TimeUnits.MILLISECONDS)));
            argumentIndex += 2;
        } while (endsWithComma);

        return new EndDateResult(true, end);
    }
}
