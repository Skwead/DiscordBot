package com.github.pauloo27.discord.bot.entity;

import com.gitlab.pauloo27.core.sql.Id;
import com.gitlab.pauloo27.core.sql.Length;
import com.gitlab.pauloo27.core.sql.Name;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Name("captchas")
public class Captcha {

    @Id
    private int id;

    @Length(64)
    private String userId;

    @Length(64)
    private String channelId;

    @Builder.Default
    @Length(18)
    private Status status = Status.WAITING;

    public enum Status {
        WAITING,
        SUCCESS,
        TIMED_OUT,
        GUILD_LEFT,
        TOO_MANY_ATTEMPTS,
        RESTARTED
    }

}
