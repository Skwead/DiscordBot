package br.com.finalelite.bots.supporter.command.commands;

import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.CommandType;
import lombok.val;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.io.IOException;

public class SayCommand extends Command {


    public SayCommand() {
        super(
                "say",
                "faz o bot enviar uma mensagem",
                CommandPermission.MAJOR_STAFF,
                CommandType.DEFAULT
        );
    }

    @Override
    public void run(Message message, Guild guild, TextChannel channel, User author, String[] args) {
        val raw = message.getContentRaw();
        val text = raw.substring(raw.indexOf(" "));
        if (text.isEmpty()) {
            sendError(channel, author, "use `!say <mensagem>`.");
            message.delete().complete();
            return;
        }

        System.out.println(message.getAttachments().size());
        if (message.getAttachments().size() > 0) {
            try {
                val attachment = message.getAttachments().get(0);
                channel.sendFile(attachment.getInputStream(), attachment.getFileName(), new MessageBuilder(text).build()).complete();
            } catch (IOException e) {
                channel.sendMessage(text).complete();
                e.printStackTrace();
            }
        } else {
            channel.sendMessage(text).complete();
        }
        message.delete().complete();
    }
}
