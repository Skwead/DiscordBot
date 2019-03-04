package br.com.finalelite.bots.supporter.command.commands.support;

import br.com.finalelite.bots.supporter.Supporter;
import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.command.CommandChannelChecker;
import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.DefaultCommandCategory;
import br.com.finalelite.bots.supporter.utils.SimpleLogger;
import lombok.val;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

public class DeleteCommand extends Command {
    public DeleteCommand() {
        super(
                "deletar",
                "deleta o ticket, enviando para o canal de logs",
                CommandPermission.SUPPORT,
                CommandChannelChecker.CLOSED_TICKET_MANAGEMENT,
                DefaultCommandCategory.SUPPORT
        );
    }

    public static void deleteTicket(Message message, Guild guild, TextChannel channel) {
        Channel logChannel;
        if (guild.getTextChannelsByName("tickets-log", false).size() == 0) {
            logChannel = guild.getController().createTextChannel("tickets-log").setParent(guild.getCategoryById(Supporter.getInstance().getConfig().getClosedCategoryId())).complete();
        } else {
            logChannel = guild.getTextChannelsByName("tickets-log", false).get(0);
        }

        val log = guild.getTextChannelById(logChannel.getId());
        val ticket = Supporter.getInstance().getDatabase().getTicketByChannelId(channel.getId());
        val base64 = Supporter.getInstance().getTicketLogger().generateLog(ticket);

        val embed = new EmbedBuilder()
                .setColor(0x23f723)
                .setTitle("Ticket Fechado - Abrir Resumo", "https://finalelite.com.br/docs/tickets/" + base64)
                .setAuthor("Final Elite", "https://finalelite.com.br", Supporter.getInstance().getJda().getSelfUser().getAvatarUrl())
                .addField("ID", String.valueOf(ticket.getId()), true)
                .addField("Nome", ticket.getName() == null ? "Não definido" : ticket.getName(), true)
                .addField("Avaliação", "Não avaliado", true)
                .addField("Base64", base64, true)
                .addField("Assunto", ticket.getSubject(), true)
                .addField("Tipo", "Não definido", true)
                .setFooter(ticket.getUser().getName() + "#" + ticket.getUser().getDiscriminator() + " - " + SimpleLogger.format(ticket.getDate()), ticket.getUser().getAvatarUrl());

        log.sendMessage(embed.build()).complete();

        guild.getTextChannelById(channel.getId()).delete().complete();
    }

    @Override
    public void run(Message message, Guild guild, TextChannel channel, User author, String[] args) {
        deleteTicket(message, guild, channel);
    }
}
