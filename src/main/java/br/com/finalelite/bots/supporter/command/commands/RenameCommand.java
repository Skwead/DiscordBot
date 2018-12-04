package br.com.finalelite.bots.supporter.command.commands;

import br.com.finalelite.bots.supporter.Main;
import br.com.finalelite.bots.supporter.command.Command;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class RenameCommand extends Command {


    public RenameCommand() {
        super("renomear", true, false, true, true);
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        val text = String.join(" ", args);
        if (text.isEmpty()) {
            sendError(textChannel, author, "use `!renomear <nome>`.", 10);
        }
        if (textChannel.getParent().getId().equals(Main.getConfig().getCategoryId()))
            textChannel.getManager().setName("\uD83D\uDC9A-ticket-" + text.replace(" ", "-")).complete();
        else if (textChannel.getName().startsWith("\uD83D\uDDA4")) {
            textChannel.getManager().setName("\uD83D\uDDA4-ticket-" + text.replace(" ", "-")).complete();
        } else {
            textChannel.getManager().setName("\uD83D\uDC97-ticket-" + text.replace(" ", "-")).complete();
        }
        message.delete().complete();
        return;
    }
}
