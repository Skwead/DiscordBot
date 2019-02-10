package br.com.finalelite.bots.supporter.command.commands.support;

import br.com.finalelite.bots.supporter.Supporter;
import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.command.CommandChannelChecker;
import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.DefaultCommandCategory;
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
                CommandPermission.SUPPORT,
                CommandChannelChecker.CLOSED_TICKET_MANAGEMENT,
                DefaultCommandCategory.SUPPORT
        );
    }

    @Override
    public void run(Message message, Guild guild, TextChannel channel, User author, String[] args) {
        val ticket = Supporter.getInstance().getDatabase().getTicketByChannelId(channel.getId());
        Supporter.getInstance().getDatabase().markTicketAsSpam(ticket);
        DeleteCommand.deleteTicket(message, guild, channel, author);
    }

}
