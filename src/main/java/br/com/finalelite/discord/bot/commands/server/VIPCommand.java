package br.com.finalelite.discord.bot.commands.server;

import br.com.finalelite.discord.bot.Bot;
import br.com.finalelite.discord.bot.entity.command.CommandBase;
import br.com.finalelite.discord.bot.entity.command.CommandChannelChecker;
import br.com.finalelite.discord.bot.entity.command.CommandPermission;
import br.com.finalelite.discord.bot.entity.command.DefaultCommandCategory;
import br.com.finalelite.discord.bot.entity.vip.VIP;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class VIPCommand extends CommandBase {

    public VIPCommand() {
        super(
                "vip",
                "ativa o VIP para o usuário a partir do ID da compra",
                CommandPermission.ADMIN,
                CommandChannelChecker.TICKET_MANAGEMENT_AND_STAFF,
                DefaultCommandCategory.SERVER
        );
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        val supporter = Bot.getInstance();
        val ticket = supporter.getDatabase().getTicketByChannelId(textChannel.getId());
        User user;
        if (textChannel.getId().equals(supporter.getConfig().getStaffChannelId())) {
            if (args.length != 2) {
                sendError(textChannel, author, "para dar paid em um VIP, use `!vip <id da compra> <discord user>`.", 10);
                message.delete().complete();
                return;
            }

            user = supporter.getJda().getUserById(args[1].substring(2, args[1].length() - 1));
        } else {
            if (args.length != 1) {
                sendError(textChannel, author, "para dar paid em um VIP, use `!vip <id da compra>`.", 10);
                message.delete().complete();
                return;
            }
            user = supporter.getJda().getUserById(ticket.getUserId());
        }
        try {
            val id = Long.parseLong(args[0]);
            val invoice = supporter.getDatabase().getInvoiceById(id);
            if (invoice == null) {
                sendError(textChannel, author, "compra não encontrada. Para dar paid em um VIP, use `!vip <id da compra>`.", 10);
                message.delete().complete();
                return;
            }
            val result = supporter.getDatabase().registerVIP(
                    VIP.builder()
                            .discordId(user.getId()).
                            invoice(invoice).build()
            );
            if (result == 1) {
                sendError(textChannel, author, "informações já usadas para ativar um VIP.", 15);
                message.delete().complete();
                return;
            }
            Bot.getInstance().getDatabase().setInvoicePaid(id);
            guild.getController().addRolesToMember(guild.getMemberById(user.getId()), guild.getRoleById(invoice.getVip().getRoleId())).complete();
            sendSuccess(textChannel, author, String.format("VIP ativado para a compra ID `%d`.", id));
            message.delete().complete();
        } catch (NumberFormatException e) {
            sendError(textChannel, author, "ID inválido. Para dar paid em um VIP, use `!vip <id da compra>`.", 10);
            message.delete().complete();
        }
    }

}
