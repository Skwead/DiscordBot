package br.com.finalelite.bots.supporter.command.commands;

import br.com.finalelite.bots.supporter.Main;
import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.ticket.Ticket;
import lombok.val;
import net.dv8tion.jda.core.entities.*;

import java.sql.SQLException;

public class SpamCommand extends Command {
    public SpamCommand() {
        super("spam", true, false, true, false, false);
    }

    @Override
    public void run(Message message, Guild guild, TextChannel channel, User author, String[] args) {
        Ticket ticket;
        try {
            ticket = Main.getDb().getTicketByChannelId(channel.getId());
            guild.getTextChannelById(Main.getConfig().getSupportChannelId()).getMessageById(ticket.getMessageId()).complete().delete().complete();
            Main.getDb().markTicketAsSpam(ticket);
            removePerms(channel, guild);
            sendSuccess(channel, author, "ticket marcado como spam.");
            message.delete().complete();
            DeleteCommand.deleteTicket(message, guild, channel, author);
        } catch (SQLException e) {
            sendError(channel, author, "um erro ocorreu ao tentar marcar o ticket como spam.");
            message.delete().complete();
            e.printStackTrace();
        }
    }

    private void removePerms(MessageChannel channel, Guild guild) {
        val targetChannel = guild.getTextChannelById(channel.getId());
        targetChannel.getManager()
                .setParent(guild.getCategoryById(Main.getConfig().getClosedCategoryId()))
                .setName("\uD83D\uDDA4-" + targetChannel.getName().substring(targetChannel.getName().indexOf("-")))
                .sync()
                .complete();
    }
}
