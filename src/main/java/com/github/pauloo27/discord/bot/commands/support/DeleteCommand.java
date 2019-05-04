package com.github.pauloo27.discord.bot.commands.support;

import com.github.pauloo27.discord.bot.Bot;
import com.github.pauloo27.discord.bot.entity.command.CommandBase;
import com.github.pauloo27.discord.bot.entity.command.CommandChannelChecker;
import com.github.pauloo27.discord.bot.entity.command.CommandPermission;
import com.github.pauloo27.discord.bot.entity.command.DefaultCommandCategory;
import com.github.pauloo27.discord.bot.entity.ticket.Ticket;
import com.github.pauloo27.discord.bot.entity.ticket.TicketRate;
import com.github.pauloo27.discord.bot.utils.SimpleLogger;
import lombok.val;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.Date;

public class DeleteCommand extends CommandBase {
    public DeleteCommand() {
        super(
                "deletar",
                "deleta o ticket, enviando para o canal de logs",
                CommandPermission.SUPPORT,
                CommandChannelChecker.CLOSED_TICKET_MANAGEMENT,
                DefaultCommandCategory.SUPPORT
        );

        Bot.getInstance().getJda().addEventListener(new ReactionListener());
    }

    public static void deleteTicket(Message message, Guild guild, TextChannel channel) {
        Channel logChannel;
        if (guild.getTextChannelsByName("tickets-log", false).size() == 0) {
            logChannel = guild.getController().createTextChannel("tickets-log").setParent(guild.getCategoryById(Bot.getInstance().getConfig().getClosedCategoryId())).complete();
        } else {
            logChannel = guild.getTextChannelsByName("tickets-log", false).get(0);
        }

        val log = guild.getTextChannelById(logChannel.getId());
        val ticket = Bot.getInstance().getDatabase().getTicketByChannelId(channel.getId());
//        val base64 = Bot.getInstance().getTicketLogger().generateLog(ticket);

        val logEmbed = createLogEmbed(ticket);

        val logMessage = log.sendMessage(logEmbed.build()).complete();

        val privateEmbed = new EmbedBuilder()
                .setColor(0x23f723)
                .setTitle("Ticket Fechado", null)
                .setTimestamp(new Date().toInstant())
                .setDescription("O seu ticket foi fechado, por favor, avalie o ticket clicando nos emojis abaixo.")
                .setAuthor("Final Elite", "https://finalelite.com.br", Bot.getInstance().getJda().getSelfUser().getAvatarUrl())
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
        Bot.getInstance().getDatabase().deleteTicket(ticket, logMessage.getId(), privateMessage == null ? null : privateMessage.getId());

    }

    private static EmbedBuilder createLogEmbed(Ticket ticket) {
        return new EmbedBuilder()
                .setColor(0x23f723)
                .setTitle("Ticket Fechado", null)
                .setAuthor("Final Elite", "https://finalelite.com.br", Bot.getInstance().getJda().getSelfUser().getAvatarUrl())
                .addField("ID", String.valueOf(ticket.getId()), true)
                .addField("Nome", ticket.getName() == null ? "Não definido" : ticket.getName(), true)
                .addField("Avaliação", ticket.getRate() == null ?
                        "Não avaliado" : ticket.getRate().getEmoji() + " " + ticket.getRate().getPortugueseName(), true)
//                .addField("Base64", base64, true)
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
            val botUser = Bot.getInstance().getJda().getSelfUser();

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

            val ticket = Bot.getInstance().getDatabase().rateTicketByMessageId(rate, event.getMessageId());

            val guild = Bot.getInstance().getJda().getGuilds().get(0);

            TextChannel logChannel;
            if (guild.getTextChannelsByName("tickets-log", false).size() == 0) {
                logChannel = (TextChannel) guild.getController().createTextChannel("tickets-log").setParent(guild.getCategoryById(Bot.getInstance().getConfig().getClosedCategoryId())).complete();
            } else {
                logChannel = guild.getTextChannelsByName("tickets-log", false).get(0);
            }

            val logMessage = logChannel.getMessageById(ticket.getLogMessageId()).complete();
//            val base64 = logMessage.getEmbeds().get(0).getFields().stream()
//                    .filter(field -> field.getName().equals("Base64"))
//                    .findFirst().orElse(null).getValue();

            val embed = createLogEmbed(ticket);

            logMessage.editMessage(embed.build()).queue();
        }
    }
}
