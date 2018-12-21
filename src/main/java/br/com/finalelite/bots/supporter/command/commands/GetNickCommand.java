package br.com.finalelite.bots.supporter.command.commands;

import br.com.finalelite.bots.supporter.Main;
import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.command.CommandPermission;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class GetNickCommand extends Command {

    public GetNickCommand() {
        super("getnick", "pega o nome do usuário usado no cadastro do site", CommandPermission.STAFF, false, true, true, true);
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        if (args.length != 1) {
            sendError(textChannel, author, "use `!getnick <id>`.", 10);
            message.delete().complete();
            return;
        }

        try {
            val id = Long.parseLong(args[0]);
            sendSuccess(textChannel, author, String.format("o nick de `%d` é `%s`.", id, Main.getDatabase().getUsername(id)));
        } catch (NumberFormatException e) {
            sendError(textChannel, author, "id inválido. Use `!getnick <id>`.", 10);
            message.delete().complete();
        }
    }

}
