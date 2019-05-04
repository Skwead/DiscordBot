package com.github.pauloo27.discord.bot.entity.ticket;

import com.github.pauloo27.discord.bot.Bot;
import com.gitlab.pauloo27.core.sql.*;
import lombok.*;
import net.dv8tion.jda.core.entities.User;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Name("tickets")
public class Ticket {
    @Id
    private int id;

    @Length(64)
    private String userId;

    @Length(64)
    private String channelId;

    @Length(64)
    private String rateMessageId;

    @Length(64)
    private String logMessageId;

    @Length(64)
    private String subject;

    @Length(32)
    private String name;

    @DefaultAttributes.NotNull
    private TicketStatus status;

    private TicketRate rate;

    @DefaultAttributes.NotNull
    private int date;

    @DefaultValue
    @Builder.Default
    private int closeDate = -1;

    public User getUser() {
        return Bot.getUserById(userId);
    }
}


