package br.com.finalelite.bots.supporter.command.commands.support;

import br.com.finalelite.bots.supporter.Supporter;
import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.command.CommandChannelChecker;
import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.DefaultCommandCategory;
import br.com.finalelite.bots.supporter.ticket.Ticket;
import br.com.finalelite.bots.supporter.ticket.TicketRate;
import br.com.finalelite.bots.supporter.utils.SimpleLogger;
import lombok.val;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.Date;

public class DeleteCommand extends Command {
    public DeleteCommand() {
        super(
                "deletar",
                "deleta o ticket, enviando para o canal de logs",
                CommandPermission.SUPPORT,
                CommandChannelChecker.CLOSED_TICKET_MANAGEMENT,
                DefaultCommandCategory.SUPPORT
        );

        Supporter.getInstance().getJda().addEventListener(new ReactionListener());
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

        val logEmbed = createLogEmbed(ticket, base64);

        val logMessage = log.sendMessage(logEmbed.build()).complete();

        val privateEmbed = new EmbedBuilder()
                .setColor(0x23f723)
                .setTitle("Ticket Fechado - Abrir Resumo", "https://finalelite.com.br/docs/tickets/" + base64)
                .setTimestamp(new Date().toInstant())
                .setDescription("O seu ticket foi fechado, por favor, avalie o ticket clicando nos emojis abaixo.")
                .setAuthor("Final Elite", "https://finalelite.com.br", Supporter.getInstance().getJda().getSelfUser().getAvatarUrl())
                .setFooter(ticket.getUser().getName() + "#" + ticket.getUser().getDiscriminator() + " - " + SimpleLogger.format(ticket.getDate()), ticket.getUser().getAvatarUrl());


        Arrays.stream(TicketRate.values()).forEach(
                ticketRate -> privateEmbed.appendDescription(String.format("\n%s - %s", ticketRate.getEmoji(), ticketRate.getPortugueseName()))
        );

        guild.getTextChannelById(channel.getId()).delete().complete();

        Message privateMessage;

        try {
            val pv = ticket.getUser().openPrivateChannel().complete();

            if (pv == null)
                return;

            privateMessage = pv.sendMessage(privateEmbed.build()).complete();

            Arrays.stream(TicketRate.values()).forEach(
                    ticketRate -> privateMessage.addReaction(ticketRate.getEmoji()).queue()
            );
        } catch (Exception e) {
            return;
        }
        Supporter.getInstance().getDatabase().deleteTicket(ticket, logMessage.getId(), privateMessage == null ? null : privateMessage.getId());

    }

    private static EmbedBuilder createLogEmbed(Ticket ticket, String base64) {
        return new EmbedBuilder()
                .setColor(0x23f723)
                .setTitle("Ticket Fechado - Abrir Resumo", "https://finalelite.com.br/docs/tickets/" + base64)
                .setAuthor("Final Elite", "https://finalelite.com.br", Supporter.getInstance().getJda().getSelfUser().getAvatarUrl())
                .addField("ID", String.valueOf(ticket.getId()), true)
                .addField("Nome", ticket.getName() == null ? "Não definido" : ticket.getName(), true)
                .addField("Avaliação", ticket.getRate() == null ?
                        "Não avaliado" : ticket.getRate().getEmoji() + " " + ticket.getRate().getPortugueseName(), true)
                .addField("Base64", base64, true)
                .addField("Assunto", ticket.getSubject(), true)
                .addField("Tipo", "Não definido", true)
                .setFooter(String.format("%s#%s - %s",
                        ticket.getUser().getName(),
                        ticket.getUser().getDiscriminator(),
                        SimpleLogger.format(ticket.getDate())),
                        ticket.getUser().getAvatarUrl()
                );
    }

    @Override
    public void run(Message message, Guild guild, TextChannel channel, User author, String[] args) {
        deleteTicket(message, guild, channel);
    }

    public class ReactionListener extends ListenerAdapter {

        @SuppressWarnings("Duplicates")
        @Override
        public void onPrivateMessageReactionAdd(PrivateMessageReactionAddEvent event) {
            val botUser = Supporter.getInstance().getJda().getSelfUser();

            if (event.getUser().getId().equals(botUser.getId()))
                return;

            val message = event.getChannel().getMessageById(event.getMessageId()).complete();


            if (message == null || !message.getAuthor().getId().equals(botUser.getId()))
                return;

            if (!message.getReactions().stream().findFirst().get().getUsers().complete().contains(botUser)) {
                return;
            }

            val rate = TicketRate.fromEmoji(event.getReaction().getReactionEmote().getName());
            if (rate == null)
                return;

            message.delete().queue();

            event.getChannel().sendMessage(":white_check_mark: Obrigado pelo seu feedback.").complete();

            val ticket = Supporter.getInstance().getDatabase().rateTicketByMessageId(rate, event.getMessageId());

            val guild = Supporter.getInstance().getJda().getGuilds().get(0);

            TextChannel logChannel;
            if (guild.getTextChannelsByName("tickets-log", false).size() == 0) {
                logChannel = (TextChannel) guild.getController().createTextChannel("tickets-log").setParent(guild.getCategoryById(Supporter.getInstance().getConfig().getClosedCategoryId())).complete();
            } else {
                logChannel = guild.getTextChannelsByName("tickets-log", false).get(0);
            }

            val logMessage = logChannel.getMessageById(ticket.getLogMessageId()).complete();
            val base64 = logMessage.getEmbeds().get(0).getFields().stream()
                    .filter(field -> field.getName().equals("Base64"))
                    .findFirst().orElse(null).getValue();

            val embed = createLogEmbed(ticket, base64);

            logMessage.editMessage(embed.build()).queue();
        }
    }
}
