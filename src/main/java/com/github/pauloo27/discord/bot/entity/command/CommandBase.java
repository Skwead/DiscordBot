package com.github.pauloo27.discord.bot.entity.command;

import com.github.pauloo27.discord.bot.Bot;
import com.github.pauloo27.discord.bot.utils.DiscordUtils;
import lombok.Data;
import net.dv8tion.jda.core.entities.*;

@Data
public abstract class CommandBase {

    private final String name;
    private final String description;
    private final CommandPermission permission;
    private final CommandChannelChecker checker;
    private final DefaultCommandCategory category;

    public Message sendError(MessageChannel channel, User user, String message) {
        return DiscordUtils.sendError(channel, user, message, -1);
    }

    public Message sendSuccess(MessageChannel channel, User user, String message) {
        return DiscordUtils.sendSuccess(channel, user, message, -1);
    }

    public Message sendError(MessageChannel channel, User user, String message, int removeSeconds) {
        return DiscordUtils.sendError(channel, user, message, removeSeconds);
    }

    public Message sendSuccess(MessageChannel channel, User user, String message, int removeSeconds) {
        return DiscordUtils.sendSuccess(channel, user, message, removeSeconds);
    }

    public String getDisplayName() {
        return Bot.getInstance().getCommandManager().getPrefix() + name;
    }

    public Message deleteAfter(int removeSeconds, Message msg) {
        return DiscordUtils.deleteAfter(removeSeconds, msg);
    }

    public abstract void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args);
}
