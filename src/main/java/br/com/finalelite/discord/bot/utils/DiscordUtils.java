package br.com.finalelite.discord.bot.utils;

import lombok.val;
import net.dv8tion.jda.core.entities.User;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class DiscordUtils {

    public static Image getUserAvatar(User user) throws IOException {
        val connection = (HttpURLConnection) new URL(user.getAvatarUrl()).openConnection();
        connection.addRequestProperty("User-Agent", "Mozilla/4.76");
        return ImageIO.read(connection.getInputStream());
    }

}
