package br.com.finalelite.discord.bot.command.commands.support;

import br.com.finalelite.discord.bot.Bot;
import br.com.finalelite.discord.bot.command.Command;
import br.com.finalelite.discord.bot.command.CommandChannelChecker;
import br.com.finalelite.discord.bot.command.CommandPermission;
import br.com.finalelite.discord.bot.command.DefaultCommandCategory;
import br.com.finalelite.discord.bot.ticket.Ticket;
import lombok.val;
import net.dv8tion.jda.core.entities.*;

public class CloseCommand extends Command {
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
