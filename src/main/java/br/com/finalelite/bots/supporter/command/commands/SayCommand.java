package br.com.finalelite.bots.supporter.command.commands;

import br.com.finalelite.bots.supporter.command.Command;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class SayCommand extends Command {


    public SayCommand() {
        super("say", true, true, true, true);
    }

    @Override
    public void run(Message message, Guild guild, TextChannel channel, User author, String[] args) {
        val text = String.join(" ", args);
        if (text.isEmpty()) {
            sendError(channel, author, "use `!say <mensagem>`.");
            message.delete().complete();
            return;
        }
        channel.sendMessage(text).complete();
    }
}
