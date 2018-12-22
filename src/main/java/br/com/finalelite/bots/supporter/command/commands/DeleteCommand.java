package br.com.finalelite.bots.supporter.command.commands;

import br.com.finalelite.bots.supporter.Supporter;
import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.command.CommandPermission;
import lombok.val;
import lombok.var;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

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
            logChannel = guild.getController().createTextChannel("tickets-log").setParent(guild.getCategoryById(Supporter.getInstance().getConfig().getClosedCategoryId())).complete();
        } else {
            logChannel = guild.getTextChannelsByName("tickets-log", false).get(0);
        }
        val log = guild.getTextChannelById(logChannel.getId());
        val ticket = Supporter.getInstance().getDatabase().getTicketByChannelId(channel.getId());
        val sb = new StringBuilder();
        val messageList = channel.getIterableHistory().complete();
        Collections.reverse(messageList);
        val name = channel.getName().startsWith("\uD83D\uDC9A") ? "\uD83D\uDDA4" + channel.getName().substring(channel.getName().indexOf("-")) : channel.getName();
        val index = new AtomicInteger();
        messageList.forEach(msg -> {
            if (msg.getAttachments().size() != 0) {
                msg.getAttachments().forEach(attachment -> {
                    try {
                        val id = index.getAndIncrement();
                        log.sendFile(attachment.getInputStream(), attachment.getFileName(), new MessageBuilder(String.format("**Anexo %d >** [%s] Ticket %d: %s", id, msg.getCreationTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a X dd/MM/yyyy")), ticket.getId(), msg.getContentRaw())).build()).complete();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } else {
                sb.append(String.format("[%s] %s (%s): %s\n", msg.getCreationTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a X dd/MM/yyyy")), msg.getAuthor().getName(), msg.getAuthor().getId(), msg.getContentRaw()));
            }
        });
        val user = Supporter.getInstance().getJda().getUserById(ticket.getUserId());
        var username = "Usuário inválido (" + ticket.getUserId() + ")";
        if (user != null)
            username = user.getAsMention();
        log.sendFile(sb.toString().getBytes(), String.format("ticket-%d.txt", ticket.getId()), new MessageBuilder(String.format("%s: %s (%d) criado por %s", name, ticket.getSubject(), ticket.getId(), username)).build()).complete();
        guild.getTextChannelById(channel.getId()).delete().complete();
    }
}
