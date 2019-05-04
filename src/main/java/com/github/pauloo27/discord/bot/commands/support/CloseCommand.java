package com.github.pauloo27.discord.bot.commands.support;

import com.github.pauloo27.discord.bot.Bot;
import com.github.pauloo27.discord.bot.entity.command.CommandBase;
import com.github.pauloo27.discord.bot.entity.command.CommandChannelChecker;
import com.github.pauloo27.discord.bot.entity.command.CommandPermission;
import com.github.pauloo27.discord.bot.entity.command.DefaultCommandCategory;
import com.github.pauloo27.discord.bot.entity.ticket.Ticket;
import lombok.val;
import net.dv8tion.jda.core.entities.*;

public class CloseCommand extends CommandBase {
    public CloseCommand() {
        super(
                "fechar",
                "fecha o ticket",
                CommandPermission.EVERYONE,
                CommandChannelChecker.OPENED_TICKET_MANAGEMENT,
                DefaultCommandCategory.SUPPORT
        );
    }

    @Override
    public void run(Message message, Guild guild, TextChannel channel, User author, String[] args) {
        val supporter = Bot.getInstance();
        val ticket = supporter.getDatabase().getTicketByChannelId(channel.getId());
        supporter.getDatabase().closeTicket(ticket);
        clearPermissions(ticket, channel, guild);
        sendSuccess(channel, author, "ticket fechado.");
        message.delete().complete();
    }

    private void clearPermissions(Ticket ticket, MessageChannel channel, Guild guild) {
        val targetChannel = guild.getTextChannelById(channel.getId());
        targetChannel.getManager()
                .setParent(guild.getCategoryById(Bot.getInstance().getConfig().getClosedCategoryId()))
                .setName(ticket.getStatus().getEmoji() + "-" + targetChannel.getName().substring(targetChannel.getName().indexOf("-")))
                .sync()
                .complete();
    }

}
