package br.com.finalelite.bots.supporter.command.commands;

import br.com.finalelite.bots.supporter.Main;
import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.ticket.Ticket;
import lombok.val;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

public class DeleteCommand extends Command {
    public DeleteCommand() {
        super("deletar", "deleta o ticket, enviando para o canal de logs", CommandPermission.STAFF, false, false, true, false);
    }

    @Override
    public void run(Message message, Guild guild, TextChannel channel, User author, String[] args) {
        deleteTicket(message, guild, channel, author);
    }

    public static void deleteTicket(Message message, Guild guild, TextChannel channel, User author) {
        Channel logChannel;
        if (guild.getTextChannelsByName("tickets-log", false).size() == 0) {
            logChannel = guild.getController().createTextChannel("tickets-log").setParent(guild.getCategoryById(Main.getConfig().getClosedCategoryId())).complete();
        } else {
            logChannel = guild.getTextChannelsByName("tickets-log", false).get(0);
        }
        val log = guild.getTextChannelById(logChannel.getId());
        Ticket ticket;
        try {
            ticket = Main.getDb().getTicketByChannelId(channel.getId());
        } catch (SQLException e) {
            sendError(channel, author, "um erro ocorreu ao tentar deletar o ticket.");
            message.delete().complete();
            e.printStackTrace();
            return;
        }
        val sb = new StringBuilder();
        val messageList = channel.getIterableHistory().complete();
        Collections.reverse(messageList);
        val name = channel.getName().startsWith("\uD83D\uDC9A") ? "\uD83D\uDDA4" + channel.getName().substring(channel.getName().indexOf("-")) : channel.getName();
        messageList.forEach(msg -> sb.append(String.format("[%s] %s (%s): %s\n", msg.getCreationTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a X dd/MM/yyyy")), msg.getAuthor().getName(), msg.getAuthor().getId(), msg.getContentRaw())));
        log.sendFile(sb.toString().getBytes(), String.format("ticket-%d.txt", ticket.getId()), new MessageBuilder(String.format("%s: %s (%d) criado por %s", name, ticket.getSubject(), ticket.getId(), Main.getJda().getUserById(ticket.getUserId()).getAsMention())).build()).complete();
        guild.getTextChannelById(channel.getId()).delete().complete();
    }
}
