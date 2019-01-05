package br.com.finalelite.bots.supporter.command.commands;

import br.com.finalelite.bots.supporter.Supporter;
import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.CommandType;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

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
        val ticket = Supporter.getInstance().getDatabase().getTicketByChannelId(channel.getId());
        guild.getTextChannelById(Supporter.getInstance().getConfig().getSupportChannelId()).getMessageById(ticket.getMessageId()).complete().delete().complete();
        Supporter.getInstance().getDatabase().markTicketAsSpam(ticket);
        DeleteCommand.deleteTicket(message, guild, channel, author);
    }

}
