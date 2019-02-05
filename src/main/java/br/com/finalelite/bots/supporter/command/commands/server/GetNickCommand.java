package br.com.finalelite.bots.supporter.command.commands.server;

import br.com.finalelite.bots.supporter.Supporter;
import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.command.CommandChannelChecker;
import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.DefaultCommandCategory;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class GetNickCommand extends Command {

    public GetNickCommand() {
        super(
                "getnick",
                "pega o nome do usuário usado no cadastro do site",
                CommandPermission.STAFF,
                CommandChannelChecker.TICKET_MANAGEMENT_AND_STAFF,
                DefaultCommandCategory.SERVER
        );
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
            sendSuccess(textChannel, author, String.format("o nick de `%d` é `%s`.", id, Supporter.getInstance().getDatabase().getUsername(id)));
        } catch (NumberFormatException e) {
            sendError(textChannel, author, "id inválido. Use `!getnick <id>`.", 10);
            message.delete().complete();
        }
    }

}
