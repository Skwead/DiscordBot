package br.com.finalelite.bots.supporter.utils;

import br.com.finalelite.bots.supporter.Supporter;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleLogger {

    private static final SimpleDateFormat formatter = new SimpleDateFormat(Supporter.getInstance().getConfig().getDateFormat());

    public static void log(String line) {
        System.out.printf("[%s] %s%n", formatter.format(new Date()), line);
    }

    public static void logMessage(TextChannel textChannel, User author, Message message, String status) {
        log(String.format("<%s> %s#%s (%s): %s %s", textChannel.getName(), author.getName(), author.getDiscriminator(), author.getId(), message.getContentRaw(), status));
    }

}
