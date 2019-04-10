package br.com.finalelite.discord.bot.entity.vip;

import com.gitlab.pauloo27.core.sql.DefaultAttributes;
import com.gitlab.pauloo27.core.sql.Length;
import com.gitlab.pauloo27.core.sql.Name;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Name("vips")
public class VIP {
    @DefaultAttributes.Unique
    @Length(64)
    private String discordId;
    @DefaultAttributes.Unique
    private Invoice invoice;
}
