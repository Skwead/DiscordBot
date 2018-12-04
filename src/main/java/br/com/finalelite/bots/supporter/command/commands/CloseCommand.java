package br.com.finalelite.bots.supporter.command.commands;

import br.com.finalelite.bots.supporter.Main;
import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.ticket.Ticket;
import lombok.val;
import net.dv8tion.jda.core.entities.*;

import java.sql.SQLException;

public class CloseCommand extends Command {
    public CloseCommand() {
        super("fechar", false, false, true, false);
    }

    @Override
    public void run(Message message, Guild guild, TextChannel channel, User author, String[] args) {
        Ticket ticket;
        try {
            ticket = Main.getDb().getTicketByChannelId(channel.getId());
            guild.getTextChannelById(Main.getConfig().getSupportChannelId()).getMessageById(ticket.getMessageId()).complete().delete().complete();
            Main.getDb().closeTicket(ticket);
            removePerms(channel, guild);
            sendSuccess(channel, author, "ticket fechado.");
            val pv = Main.getJda().getUserById(ticket.getUserId()).openPrivateChannel().complete();
            message.delete().complete();
            if (pv == null)
                return;
//                pv.sendMessage("").complete();
        } catch (SQLException e) {
            sendError(channel, author, "um erro ocorreu ao tentar fechar o ticket.", 10);
            message.delete().complete();
            e.printStackTrace();
        }
    }

    private void removePerms(MessageChannel channel, Guild guild) {
        val targetChannel = guild.getTextChannelById(channel.getId());
        targetChannel.getManager()
                .setParent(guild.getCategoryById(Main.getConfig().getClosedCategoryId()))
                .setName("\uD83D\uDC97-" + targetChannel.getName().substring(targetChannel.getName().indexOf("-")))
                .sync()
                .complete();
    }
}
