package br.com.finalelite.bots.supporter.command.commands.server;

import br.com.finalelite.bots.supporter.Supporter;
import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.CommandType;
import br.com.finalelite.bots.supporter.command.DefaultCommandCategory;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class GetDiscordCommand extends Command {

    public GetDiscordCommand() {
        super(
                "getdiscord",
                "pega o Discord usado na ativação de um VIP a partir do ID da compra",
                CommandPermission.STAFF,
                CommandType.TICKET_MANAGEMENT_AND_STAFF,
                DefaultCommandCategory.SERVER.getCategory()
        );
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        if (args.length != 1) {
            sendError(textChannel, author, "use `!getdiscord <id>`.", 10);
            message.delete().complete();
            return;
        }

        try {
            val id = Long.parseLong(args[0]);
            sendSuccess(textChannel, author, String.format("o Discord de `%d` é <@%s> (`%s`).", id, Supporter.getInstance().getDatabase().getDiscordIdByInvoiceId(id), Supporter.getInstance().getDatabase().getDiscordIdByInvoiceId(id)));
        } catch (NumberFormatException e) {
            sendError(textChannel, author, "id inválido. Use `!getdiscord <id>`.", 10);
            message.delete().complete();
        }
    }

}
