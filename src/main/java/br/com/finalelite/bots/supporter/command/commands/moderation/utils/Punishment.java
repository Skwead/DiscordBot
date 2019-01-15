package br.com.finalelite.bots.supporter.command.commands.moderation.utils;

import lombok.Builder;
import lombok.Data;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Date;

@Builder
@Data
public class Punishment {

    private final int id;
    private final Date date;
    private final Member author;
    private final Member target;
    private final Guild relatedGuild;
    private final TextChannel relatedChannel;
    private final Message relatedMessage;
    private final PunishmentType type;
    private final String reason;
    private final Date end;
    private final boolean reverted;

    public void apply() {
        type.apply(this);
    }

}
