package br.com.finalelite.bots.supporter.command.commands;

import br.com.finalelite.bots.supporter.Main;
import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.ticket.Ticket;
import com.google.common.io.Files;
import lombok.val;
import net.dv8tion.jda.core.entities.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

public class DeleteCommand extends Command {
    public DeleteCommand() {
        super("deletar", true, false, false, true, false);
    }

    @Override
    public void run(Message message, Guild guild, TextChannel channel, User author, String[] args) {
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
        val tempFile = new File("tmp/history-" + ticket.getId() + ".txt");
        try {
            Files.createParentDirs(tempFile);
        } catch (IOException e) {
            sendError(channel, author, "um erro ocorreu ao tentar criar a log.");
            message.delete().complete();
            e.printStackTrace();
            return;
        }
        try {
            tempFile.createNewFile();
        } catch (IOException e) {
            sendError(channel, author, "um erro ocorreu ao tentar criar a log.");
            message.delete().complete();
            e.printStackTrace();
            return;
        }
        try {
            val writer = Files.newWriter(tempFile, StandardCharsets.UTF_8);
            val list = channel.getIterableHistory().complete();
            Collections.reverse(list);
            list.stream().forEach((msg) -> {
                try {
                    writer.write(String.format("[%s] %s (%s): %s\n", msg.getCreationTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a X dd/MM/yyyy")), msg.getAuthor().getName(), msg.getAuthor().getId(), msg.getContentRaw()));
                } catch (IOException e) {
                    sendError(channel, author, "um erro ocorreu ao tentar criar a log.");
                    e.printStackTrace();
                    message.delete().complete();
                }
            });
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            sendError(channel, author, "um erro ocorreu ao tentar criar a log.");
            message.delete().complete();
            e.printStackTrace();
            return;
        }
        log.sendFile(tempFile).complete();
        tempFile.delete();
        guild.getTextChannelById(channel.getId()).delete().complete();
    }
}
