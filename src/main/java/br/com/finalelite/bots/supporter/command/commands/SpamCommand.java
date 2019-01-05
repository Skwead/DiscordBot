package br.com.finalelite.bots.supporter.command.commands;

import br.com.finalelite.bots.supporter.Main;
import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.CommandType;
import br.com.finalelite.bots.supporter.ticket.Ticket;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.sql.SQLException;

public class SpamCommand extends Command {

    public SpamCommand() {
        super(
                "spam",
                "marca um ticket como spam e o deleta",
                CommandPermission.STAFF,
                CommandType.CLOSED_TICKET_MANAGEMENT
        );
    }

    @Override
    public void run(Message message, Guild guild, TextChannel channel, User author, String[] args) {
        Ticket ticket;
        try {
            ticket = Main.getDb().getTicketByChannelId(channel.getId());
            guild.getTextChannelById(Main.getConfig().getSupportChannelId()).getMessageById(ticket.getMessageId()).complete().delete().complete();
            Main.getDb().markTicketAsSpam(ticket);
            DeleteCommand.deleteTicket(message, guild, channel, author);
        } catch (SQLException e) {
            sendError(channel, author, "um erro ocorreu ao tentar marcar o ticket como spam.");
            message.delete().complete();
            e.printStackTrace();
        }
    }

}
