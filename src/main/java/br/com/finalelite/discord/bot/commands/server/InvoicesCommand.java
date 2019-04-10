package br.com.finalelite.discord.bot.commands.server;

import br.com.finalelite.discord.bot.Bot;
import br.com.finalelite.discord.bot.entity.command.Command;
import br.com.finalelite.discord.bot.entity.command.CommandChannelChecker;
import br.com.finalelite.discord.bot.entity.command.CommandPermission;
import br.com.finalelite.discord.bot.entity.command.DefaultCommandCategory;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class InvoicesCommand extends Command {
    public InvoicesCommand() {
        super(
                "compras",
                "lista as compras feitas pelo usuário a partir do email",
                CommandPermission.ADMIN,
                CommandChannelChecker.TICKET_MANAGEMENT_AND_STAFF,
                DefaultCommandCategory.SERVER
        );
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        if (args.length != 1) {
            sendError(textChannel, author, "use `!compras <email>`.", 10);
            deleteAfter(10, message);
            return;
        }
        val email = args[0];
        val result = Bot.getInstance().getDatabase().getInvoicesByEmail(email);
        if (result == null || result.size() == 0) {
            sendError(textChannel, author, "nenhuma compra cadastrada no email `" + email + "`.");
            deleteAfter(10, message);
            return;
        }

        val sb = new StringBuilder();
        sb.append("**ID do usuário: ");
        sb.append(result.get(0).getUserId());
        sb.append("**\n");
        result.forEach((invoice -> {
            sb.append(invoice.getId());
            sb.append(" - ");
            sb.append(invoice.getVip().name());
            sb.append(" ");
            sb.append(invoice.getType().toPtBR().toUpperCase());
            sb.append(invoice.isPaid() ? ": :white_check_mark:" : ": :x:");
            sb.append("\n");
        }));

        textChannel.sendMessage(sb.toString()).complete();
        message.delete().complete();
    }

}
