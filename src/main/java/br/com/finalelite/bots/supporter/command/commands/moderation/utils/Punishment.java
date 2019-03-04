package br.com.finalelite.bots.supporter.command.commands.moderation.utils;

import br.com.finalelite.bots.supporter.Supporter;
import com.gitlab.pauloo27.core.sql.*;
import lombok.*;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Name("punishments")
public class Punishment {

    @Id
    private int id;

    @DefaultAttributes.NotNull
    @Name("date")
    private int dateSeconds;

    @DefaultAttributes.NotNull
    @Length(64)
    private String authorId;

    @DefaultAttributes.NotNull
    @Length(64)
    private String targetId;

    @DefaultAttributes.NotNull
    @Length(64)
    private String relatedGuildId;

    @Length(64)
    private String relatedChannelId;

    @Length(64)
    private String relatedMessageId;

    @DefaultAttributes.NotNull
    private PunishmentType type;

    @DefaultAttributes.NotNull
    @Length(256)
    @Builder.Default
    private String reason = "Nenhum motivo informado";

    @DefaultAttributes.NotNull
    @Name("end")
    private int endSeconds;

    @DefaultAttributes.NotNull
    @DefaultValue
    @Builder.Default
    private boolean reverted = false;

    public static int parseDate(Date date) {
        if (date == null)
            return -1;
        return (int) (date.getTime() / 1000);
    }

    public void apply() {
        type.apply(this);
    }

    public void revert() {
        type.revert(this);
    }

    public Guild getRelatedGuild() {
        return Supporter.getGuildById(relatedGuildId);
    }

    public TextChannel getRelatedChannel() {
        return Supporter.getTextChannelById(relatedChannelId);
    }

    public Message getRelatedMessage() {
        return Supporter.getMessageById(relatedChannelId, relatedMessageId);
    }

    public Member getAuthor() {
        return Supporter.getMemberById(relatedGuildId, authorId);
    }

    public Member getTarget() {
        return Supporter.getMemberById(relatedGuildId, targetId);
    }

    public Date getDate() {
        return new Date(((long) dateSeconds) * 1000);
    }

    public Date getEnd() {
        return new Date(((long) endSeconds) * 1000);
    }

}
