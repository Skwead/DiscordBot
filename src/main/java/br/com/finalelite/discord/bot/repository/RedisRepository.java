package br.com.finalelite.discord.bot.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.dv8tion.jda.core.JDA;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


@AllArgsConstructor
@Data
public abstract class RedisRepository {

    private JedisPool pool;
    private JDA jda;

    public RedisRepository(JDA jda) {
        this(RedisPool.DEFAULT, jda);
    }

    protected Jedis getResource() {
        return pool.getResource();
    }

}
