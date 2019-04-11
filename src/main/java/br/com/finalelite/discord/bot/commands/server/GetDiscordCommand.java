package br.com.finalelite.discord.bot.commands.server;

import br.com.finalelite.discord.bot.Bot;
import br.com.finalelite.discord.bot.entity.command.CommandBase;
import br.com.finalelite.discord.bot.entity.command.CommandChannelChecker;
import br.com.finalelite.discord.bot.entity.command.CommandPermission;
import br.com.finalelite.discord.bot.entity.command.DefaultCommandCategory;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class GetDiscordCommand extends CommandBase {

    public GetDiscordCommand() {
        super(
                "getdiscord",
                "pega o Discord usado na ativação de um VIP a partir do ID da compra",
                CommandPermission.ADMIN,
                CommandChannelChecker.TICKET_MANAGEMENT_AND_STAFF,
                DefaultCommandCategory.SERVER
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
            sendSuccess(textChannel, author, String.format("o Discord de `%d` é <@%s> (`%s`).", id, Bot.getInstance().getDatabase().getDiscordIdByInvoiceId(id), Bot.getInstance().getDatabase().getDiscordIdByInvoiceId(id)));
        } catch (NumberFormatException e) {
            sendError(textChannel, author, "id inválido. Use `!getdiscord <id>`.", 10);
            message.delete().complete();
        }
    }

}
