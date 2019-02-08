package br.com.finalelite.bots.supporter.command.commands.moderation;

import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.commands.moderation.utils.PunishmentCommand;
import br.com.finalelite.bots.supporter.command.commands.moderation.utils.PunishmentType;
import br.com.finalelite.bots.supporter.utils.time.TimeUnits;

import java.util.Date;

public class WarnCommand extends PunishmentCommand {

    public WarnCommand() {
        super("warn", "repreende um usuário no Discord", CommandPermission.STAFF, PunishmentType.WARN);
    }

    @Override
    public Date getDefaultEndDate(Date now) {
        return new Date((now.getTime() + (long) TimeUnits.HOURS.convert(48, TimeUnits.MILLISECONDS)));
    }
}
