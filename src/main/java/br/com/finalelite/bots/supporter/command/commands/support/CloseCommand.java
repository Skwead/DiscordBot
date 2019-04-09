package br.com.finalelite.bots.supporter.command.commands.support;

import br.com.finalelite.bots.supporter.Supporter;
import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.command.CommandChannelChecker;
import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.DefaultCommandCategory;
import br.com.finalelite.bots.supporter.ticket.Ticket;
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
        val supporter = Supporter.getInstance();
        val ticket = supporter.getDatabase().getTicketByChannelId(channel.getId());
        supporter.getDatabase().closeTicket(ticket);
        clearPermissions(ticket, channel, guild);
        sendSuccess(channel, author, "ticket fechado.");
        message.delete().complete();
    }

    private void clearPermissions(Ticket ticket, MessageChannel channel, Guild guild) {
        val targetChannel = guild.getTextChannelById(channel.getId());
        targetChannel.getManager()
                .setParent(guild.getCategoryById(Supporter.getInstance().getConfig().getClosedCategoryId()))
                .setName(ticket.getStatus().getEmoji() + "-" + targetChannel.getName().substring(targetChannel.getName().indexOf("-")))
                .sync()
                .complete();
    }

}
