package com.github.pauloo27.discord.bot.entity.punishment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Rule {
    private String description;
    private PunishmentType type;
    private long timeInSeconds;
    private boolean nsfw;
    private String emoji;
}
