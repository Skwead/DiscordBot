package br.com.finalelite.discord.bot.utils;

import br.com.finalelite.discord.bot.Bot;
import lombok.val;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DiscordUtils {

    private static Map<Message, Long> DELETE_QUEUE = new HashMap<>();

    static {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (DELETE_QUEUE.isEmpty()) continue;

                Map<Message, Long> newQueue = new HashMap<>(DELETE_QUEUE);
                Map<Message, Long> currentQueue = new HashMap<>(DELETE_QUEUE);
                currentQueue.forEach((message, time) -> {
                    if (Bot.getTextChannelById(message.getTextChannel().getId()) == null) {
                        newQueue.remove(message);
                    } else {
                        if (System.currentTimeMillis() > time) {
                            try {

                                message.delete().complete();
                            } catch (Exception e) {

                            } finally {
                                newQueue.remove(message);
                            }
                        }
                    }
                });
                DELETE_QUEUE = newQueue;
            }
        }).start();
    }

    public static Message deleteAfter(int afterSeconds, Message message) {
        if (afterSeconds == -1) return message;

        DELETE_QUEUE.put(message, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(afterSeconds));
        return message;
    }


    public static Image getUserAvatar(User user) throws IOException {
        val connection = (HttpURLConnection) new URL(user.getAvatarUrl()).openConnection();
        connection.addRequestProperty("User-Agent", "Mozilla/4.76");
        return ImageIO.read(connection.getInputStream());
    }

}
