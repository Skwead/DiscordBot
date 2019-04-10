package br.com.finalelite.discord.bot.entity.command;

import br.com.finalelite.discord.bot.utils.DiscordUtils;
import lombok.Data;
import lombok.val;
import net.dv8tion.jda.core.entities.*;

@Data
public abstract class Command {

    private final String name;
    private final String description;
    private final CommandPermission permission;
    private final CommandChannelChecker checker;
    private final DefaultCommandCategory category;

    public Message sendError(MessageChannel channel, User user, String message) {
        return sendError(channel, user, message, -1);
    }

    public Message sendSuccess(MessageChannel channel, User user, String message) {
        return sendSuccess(channel, user, message, -1);
    }

    public Message sendError(MessageChannel channel, User user, String message, int removeSeconds) {
        val msg = channel.sendMessage(String.format(":x: %s, %s", user.getAsMention(), message)).complete();
        return deleteAfter(removeSeconds, msg);
    }

    public Message sendSuccess(MessageChannel channel, User user, String message, int removeSeconds) {
        val msg = channel.sendMessage(String.format(":white_check_mark: %s, %s", user.getAsMention(), message)).complete();
        return deleteAfter(removeSeconds, msg);
    }

    public Message deleteAfter(int removeSeconds, Message msg) {
        return DiscordUtils.deleteAfter(removeSeconds, msg);
    }

    public abstract void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args);
}
