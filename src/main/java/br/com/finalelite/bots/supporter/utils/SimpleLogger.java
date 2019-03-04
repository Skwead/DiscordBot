package br.com.finalelite.bots.supporter.utils;

import br.com.finalelite.bots.supporter.Supporter;
import lombok.val;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SimpleLogger {

    private static final SimpleDateFormat formatter;
//    private static Logger logger = JDALogger.getLog("BOT");

    static {
        if (Supporter.getInstance().getConfig() != null)
            formatter = new SimpleDateFormat(Supporter.getInstance().getConfig().getDateFormat());
        else
            formatter = new SimpleDateFormat("HH:mm:ss Z yyyy/MM/dd");
    }

    public static String format(Date date) {
        return formatter.format(date);
    }

    public static String format(int date) {
        return formatter.format(new Date(date * 1000L));
    }

    public static void log(String line) {
//        logger.info(line);
        System.out.printf("[%s] %s%n", format(new Date()), line);
    }

    public static void log(String formatLine, Object... values) {
        log(String.format(formatLine, values));
    }

    public static void logMessage(TextChannel textChannel, User author, Message message, String status) {
        log(String.format("<%s> %s#%s (%s): %s %s", textChannel.getName(), author.getName(), author.getDiscriminator(), author.getId(), message.getContentRaw(), status));
    }

    public static void sendLogToOwner(String message) {
        val pv = Supporter.getInstance().getJda().getUserById(Supporter.getInstance().getConfig().getOwnerId()).openPrivateChannel().complete();
        pv.sendMessage(message).complete();
    }

    public static void sendStackTraceToOwner(Exception e) {
        val sb = new StringBuilder();
        sb.append("**Look, a poem:**\n");
        sb.append(e.getMessage()).append("\n");
        Arrays.stream(e.getStackTrace()).forEach(stackTraceElement -> sb.append(stackTraceElement.toString()).append("\n"));
        val lines = Arrays.asList(sb.toString().split("\n"));
        val times = lines.size() / 10;
        IntStream.range(0, times == 0 ? 1 : times).forEach(time -> {
            val tempLines = lines.stream().skip(time * 10).limit(10).collect(Collectors.toCollection(ArrayList::new));
            tempLines.add(time == 0 ? 1 : 0, "```java");
            tempLines.add("```");
            SimpleLogger.sendLogToOwner(String.join("\n", tempLines));
        });
    }
}
