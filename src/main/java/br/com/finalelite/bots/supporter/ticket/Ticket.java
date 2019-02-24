package br.com.finalelite.bots.supporter.ticket;

import com.gitlab.pauloo27.core.sql.DefaultAttributes;
import com.gitlab.pauloo27.core.sql.Id;
import com.gitlab.pauloo27.core.sql.Length;
import com.gitlab.pauloo27.core.sql.Name;
import lombok.*;

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

    @DefaultAttributes.NotNull
    private TicketStatus status;
}


