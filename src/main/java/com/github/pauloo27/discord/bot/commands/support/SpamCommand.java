package com.github.pauloo27.discord.bot.commands.support;

import com.github.pauloo27.discord.bot.Bot;
import com.github.pauloo27.discord.bot.entity.command.CommandBase;
import com.github.pauloo27.discord.bot.entity.command.CommandChannelChecker;
import com.github.pauloo27.discord.bot.entity.command.CommandPermission;
import com.github.pauloo27.discord.bot.entity.command.DefaultCommandCategory;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class SpamCommand extends CommandBase {

    public SpamCommand() {
        super(
                "spam",
                "marca um ticket como spam e o deleta",
                CommandPermission.SUPPORT,
                CommandChannelChecker.CLOSED_TICKET_MANAGEMENT,
                DefaultCommandCategory.SUPPORT
        );
    }

    @Override
    public void run(Message message, Guild guild, TextChannel channel, User author, String[] args) {
        val ticket = Bot.getInstance().getDatabase().getTicketByChannelId(channel.getId());
        Bot.getInstance().getDatabase().markTicketAsSpam(ticket);
        DeleteCommand.deleteTicket(message, guild, channel);
    }

}
