package br.com.finalelite.bots.supporter.command;

import lombok.Data;
import lombok.val;
import net.dv8tion.jda.core.entities.*;

@Data
public abstract class Command {

    private final String name;
    private final String description;
    private final boolean isStaffOnly;
    private final boolean usableInSupportChannel;
    private final boolean usableInOpenedCategory;
    private final boolean usableInClosedCategory;
    private final boolean usableInStaffChannel;

    public abstract void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args);

    public static Message sendError(MessageChannel channel, User user, String message) {
        return sendError(channel, user, message, -1);
    }

    public static Message sendSuccess(MessageChannel channel, User user, String message) {
        return sendSuccess(channel, user, message, -1);
    }

    public static Message sendError(MessageChannel channel, User user, String message, int removeSeconds) {
        val msg = channel.sendMessage(String.format(":x: %s, %s", user.getAsMention(), message)).complete();
        return deleteAfter(removeSeconds, msg);
    }

    public static Message sendSuccess(MessageChannel channel, User user, String message, int removeSeconds) {
        val msg = channel.sendMessage(String.format(":white_check_mark: %s, %s", user.getAsMention(), message)).complete();
        return deleteAfter(removeSeconds, msg);
    }

    public static Message deleteAfter(int removeSeconds, Message msg) {
        if (removeSeconds == -1)
            return msg;

        new Thread(() -> {
            try {
                Thread.sleep(removeSeconds * 1000);
                msg.delete().complete();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        return msg;
    }
}
