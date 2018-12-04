package br.com.finalelite.bots.supporter.command.commands;

import br.com.finalelite.bots.supporter.Main;
import br.com.finalelite.bots.supporter.command.Command;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.sql.SQLException;

public class VIPCommand extends Command {

    public VIPCommand() {
        super("vip", true, false, true, true);
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        if (args.length != 1) {
            sendError(textChannel, author, "para dar paid em um VIP, use `!vip <id da compra>`.", 10);
            message.delete().complete();
            return;
        }

        try {
            val id = Long.parseLong(args[0]);
            val invoice = Main.getDb().getInvoiceById(id);
            val ticket = Main.getDb().getTicketByChannelId(textChannel.getId());
            Main.getDb().setInvoicePaid(id);
            guild.getController().addRolesToMember(guild.getMemberById(ticket.getUserId()), guild.getRoleById(invoice.getVip().getRoleId())).complete();
            sendSuccess(textChannel, author, String.format("VIP ativado para a compra ID `%d`.", id));
            message.delete().complete();
        } catch (NumberFormatException e) {
            sendError(textChannel, author, "ID inválido. Para dar paid em um VIP, use `!vip <id da compra>`.", 10);
            message.delete().complete();
        } catch (SQLException e) {
            sendError(textChannel, author, "um erro ocorreu.", 10);
            message.delete().complete();
            e.printStackTrace();
        }

    }

}
