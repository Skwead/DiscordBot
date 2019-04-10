package br.com.finalelite.discord.bot.commands.utils;

import br.com.finalelite.discord.bot.entity.command.Command;
import br.com.finalelite.discord.bot.entity.command.CommandChannelChecker;
import br.com.finalelite.discord.bot.entity.command.CommandPermission;
import br.com.finalelite.discord.bot.entity.command.DefaultCommandCategory;
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
                CommandPermission.MANAGER,
                CommandChannelChecker.DISABLE,
                DefaultCommandCategory.UTILS
        );
    }

    @Override
    public void run(Message message, Guild guild, TextChannel channel, User author, String[] args) {
        if (args.length == 0) {
            sendError(channel, author, "use `!say <mensagem>`.");
            message.delete().complete();
            return;
        }

        val rawText = message.getContentRaw();
        val text = rawText.substring(rawText.indexOf(" "));

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
