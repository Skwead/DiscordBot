package br.com.finalelite.bots.supporter.ticket;

import br.com.finalelite.bots.supporter.Supporter;
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
    private String subject;

    @Length(32)
    private String name;

    @DefaultAttributes.NotNull
    private TicketStatus status;

    @DefaultAttributes.NotNull
    private int date;

    @DefaultValue
    @Builder.Default
    private int closeDate = -1;

    public User getUser() {
        return Supporter.getUserById(userId);
    }
}


