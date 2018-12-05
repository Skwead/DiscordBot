package br.com.finalelite.bots.supporter.command.commands;

import br.com.finalelite.bots.supporter.Main;
import br.com.finalelite.bots.supporter.command.Command;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class UserIdCommand extends Command {

    public UserIdCommand() {
        super("getid", true, false, true, true, true);
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        if (args.length != 1) {
            sendError(textChannel, author, "use `!getid <email>`.", 15);
            message.delete().complete();
            return;
        }

        val email = args[0];
        val x = Main.getDb().getUserIdByEmail(email);
        if (x == -1) {
            sendError(textChannel, author, String.format("nenhum usuário não registrado com o email `%s`.", email));
            message.delete().complete();
            return;
        }
        sendSuccess(textChannel, author, String.format("o ID do usuário registrado com o email `%s` é .", email, x));
        message.delete().complete();
    }

}
