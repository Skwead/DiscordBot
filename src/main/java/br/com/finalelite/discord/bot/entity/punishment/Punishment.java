package br.com.finalelite.discord.bot.entity.punishment;

import br.com.finalelite.discord.bot.Bot;
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
    @Length(256)
    @Builder.Default
    private String proof = "Nenhuma prova informada";

    @DefaultAttributes.NotNull
    @Name("end")
    private int endSeconds;

    @Length(64)
    private String revertedById;

    public static int parseDate(Date date) {
        if (date == null)
            return -1;
        return (int) (date.getTime() / 1000);
    }

    @DefaultValue
    @Builder.Default
    private boolean nsfw = false;

    public void apply() {
        type.apply(this);
    }

    public void revert() {
        type.revert(this);
    }

    public Guild getRelatedGuild() {
        return Bot.getGuildById(relatedGuildId);
    }

    public TextChannel getRelatedChannel() {
        return Bot.getTextChannelById(relatedChannelId);
    }

    public Message getRelatedMessage() {
        return Bot.getMessageById(relatedChannelId, relatedMessageId);
    }

    public Member getAuthor() {
        return Bot.getMemberById(relatedGuildId, authorId);
    }

    public Member getTarget() {
        return Bot.getMemberById(relatedGuildId, targetId);
    }

    public Date getDate() {
        return new Date(((long) dateSeconds) * 1000);
    }

    public Date getEnd() {
        return new Date(((long) endSeconds) * 1000);
    }

    public Member getRevertedBy() {
        return Bot.getMemberById(relatedGuildId, revertedById);
    }

}
