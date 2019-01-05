package br.com.finalelite.bots.supporter.command.commands;

import br.com.finalelite.bots.supporter.Main;
import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.CommandType;
import br.com.finalelite.bots.supporter.ticket.Ticket;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.sql.SQLException;

public class VIPCommand extends Command {

    public VIPCommand() {
        super(
                "vip",
                "ativa o VIP para o usuário a partir do ID da compra",
                CommandPermission.MAJOR_STAFF,
                CommandType.TICKET_MANAGEMENT_AND_STAFF
        );
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        Ticket ticket;
        try {
            ticket = Main.getDb().getTicketByChannelId(textChannel.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        User user;
        if (textChannel.getId().equals(Main.getConfig().getStaffChannelId())) {
            if (args.length != 2) {
                sendError(textChannel, author, "para dar paid em um VIP, use `!vip <id da compra> <discord user>`.", 10);
                message.delete().complete();
                return;
            }

            user = Main.getJda().getUserById(args[1].substring(2, args[1].length() - 1));
        } else {
            if (args.length != 1) {
                sendError(textChannel, author, "para dar paid em um VIP, use `!vip <id da compra>`.", 10);
                message.delete().complete();
                return;
            }
            user = Main.getJda().getUserById(ticket.getUserId());
        }
        try {
            val id = Long.parseLong(args[0]);
            val invoice = Main.getDb().getInvoiceById(id);
            if (invoice == null) {
                sendError(textChannel, author, "compra não encontrada. Para dar paid em um VIP, use `!vip <id da compra>`.", 10);
                message.delete().complete();
                return;
            }
            val result = Main.getDb().registerVIP(user.getId(), invoice.getId());
            if (result == 1) {
                sendError(textChannel, author, "informações já usadas para ativar um VIP.", 15);
                message.delete().complete();
                return;
            }
//            Main.getDb().setInvoicePaid(id);
            guild.getController().addRolesToMember(guild.getMemberById(user.getId()), guild.getRoleById(invoice.getVip().getRoleId())).complete();
            sendSuccess(textChannel, author, String.format("VIP ativado para a compra ID `%d`.", id));
            message.delete().complete();
        } catch (NumberFormatException e) {
            sendError(textChannel, author, "ID inválido. Para dar paid em um VIP, use `!vip <id da compra>`.", 10);
            message.delete().complete();
        }
    }

}
