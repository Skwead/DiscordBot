package com.github.pauloo27.discord.bot.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.core.entities.Game;

@AllArgsConstructor
@Getter
@Setter
public class Presence {
    private Game.GameType type;
    private String label;
    private String url;

    public Game toGame() {
        return Game.of(type, label, url);
    }
}
