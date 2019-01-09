package br.com.finalelite.bots.supporter.command.commands.support.messages;

import br.com.finalelite.bots.supporter.ticket.Ticket;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

@FunctionalInterface
public interface PlaceHolder {
    String get(Ticket ticket, Message message, Guild guild, TextChannel channel, User author);
}
