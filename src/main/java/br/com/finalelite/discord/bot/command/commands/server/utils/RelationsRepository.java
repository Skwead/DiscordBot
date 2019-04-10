package br.com.finalelite.discord.bot.command.commands.server.utils;

import br.com.finalelite.discord.bot.repository.RedisRepository;
import lombok.var;
import net.dv8tion.jda.core.JDA;
import redis.clients.jedis.Jedis;

import java.util.concurrent.ThreadLocalRandom;

public class RelationsRepository extends RedisRepository {

    public RelationsRepository(JDA jda) {
        super(jda);
    }

    public String storeGeneratedCode(String id) {
        try (Jedis client = getResource()) {
            var code = ThreadLocalRandom.current().nextInt(1000000, 9999999);

            while (client.exists("discord_key#" + code)) {
                code = ThreadLocalRandom.current().nextInt(1000000, 9999999);
            }

            client.hset("discord_key#" + code, "id", id);
            client.expire("discord_key#" + code, 300);

            return String.valueOf(code);
        }

    }
}