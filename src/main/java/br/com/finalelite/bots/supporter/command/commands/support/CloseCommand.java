package br.com.finalelite.bots.supporter.command.commands.support;

import br.com.finalelite.bots.supporter.Supporter;
import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.CommandType;
import br.com.finalelite.bots.supporter.command.DefaultCommandCategory;
import lombok.val;
import net.dv8tion.jda.core.entities.*;

public class CloseCommand extends Command {
    public CloseCommand() {
        super(
                "fechar",
                "fecha o ticket",
                CommandPermission.EVERYONE,
                CommandType.OPENED_TICKET_MANAGEMENT,
                DefaultCommandCategory.SUPPORT.getCategory()
        );
    }

    @Override
    public void run(Message message, Guild guild, TextChannel channel, User author, String[] args) {
        val supporter = Supporter.getInstance();
        val ticket = supporter.getDatabase().getTicketByChannelId(channel.getId());
        guild.getTextChannelById(supporter.getConfig().getSupportChannelId()).getMessageById(ticket.getMessageId()).complete().delete().complete();
        supporter.getDatabase().closeTicket(ticket);
        clearPermissions(channel, guild);
        sendSuccess(channel, author, "ticket fechado.");
        val pv = supporter.getJda().getUserById(ticket.getUserId()).openPrivateChannel().complete();
        message.delete().complete();
        if (pv == null)
            return;
    }

    private void clearPermissions(MessageChannel channel, Guild guild) {
        val targetChannel = guild.getTextChannelById(channel.getId());
        targetChannel.getManager()
                .setParent(guild.getCategoryById(Supporter.getInstance().getConfig().getClosedCategoryId()))
                .setName("\uD83D\uDC97-" + targetChannel.getName().substring(targetChannel.getName().indexOf("-")))
                .sync()
                .complete();
    }

}
