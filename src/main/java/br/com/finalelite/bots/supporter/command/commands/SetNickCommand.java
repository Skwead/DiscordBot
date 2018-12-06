package br.com.finalelite.bots.supporter.command.commands;

import br.com.finalelite.bots.supporter.Main;
import br.com.finalelite.bots.supporter.command.Command;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class SetNickCommand extends Command {

    public SetNickCommand() {
        super("setnick", true, false, true, true, true);
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        if (args.length != 2) {
            sendError(textChannel, author, "use `!setnick <id do usuario> <novo nome>`.", 10);
            message.delete().complete();
            return;
        }

        val newName = args[1];
        try {
            val id = Long.parseLong(args[0]);
            val result = Main.getDb().setUsername(id, newName);
            if (result == 1) {
                sendError(textChannel, author, String.format("o nick %s já está em uso.", newName));
                return;
            }
            sendSuccess(textChannel, author, "nick alterado.");
        } catch (NumberFormatException e) {
            sendError(textChannel, author, "id inválido. Use `!setnick <id do usuario> <novo nome>`.", 10);
            message.delete().complete();
        }
    }

}
