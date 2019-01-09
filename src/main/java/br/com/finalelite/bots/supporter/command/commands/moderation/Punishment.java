package br.com.finalelite.bots.supporter.command.commands.moderation;

import lombok.Data;
import net.dv8tion.jda.core.entities.Member;

import java.util.Date;

@Data
public class Punishment {

    private final int id;
    private final Date date;
    private final Member author;
    private final Member target;
    private final PunishmentType type;
    private final String reason;
    private final Date end;
    private final boolean reverted;

}
