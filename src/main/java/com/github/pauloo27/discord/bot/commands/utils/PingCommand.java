package com.github.pauloo27.discord.bot.commands.utils;

import com.github.pauloo27.discord.bot.Bot;
import com.github.pauloo27.discord.bot.entity.command.CommandBase;
import com.github.pauloo27.discord.bot.entity.command.CommandChannelChecker;
import com.github.pauloo27.discord.bot.entity.command.CommandPermission;
import com.github.pauloo27.discord.bot.entity.command.DefaultCommandCategory;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class PingCommand extends CommandBase {
    public PingCommand() {
        super(
                "ping",
                "retorna a latência do bot",
                CommandPermission.BOT_OWNER,
                CommandChannelChecker.DISABLE,
                DefaultCommandCategory.UTILS
        );
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        sendSuccess(textChannel, author, String.format("o meu ping é %d.", Bot.getInstance().getJda().getPing()));
        message.delete().complete();
    }
}
