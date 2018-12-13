package br.com.finalelite.bots.supporter.command.commands;

import br.com.finalelite.bots.supporter.Main;
import br.com.finalelite.bots.supporter.command.Command;
import lombok.val;
import lombok.var;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.sql.SQLException;

public class SupportCommand extends Command {
    public SupportCommand() {
        super("suporte", false, true, false, false, false);
    }

    @Override
    public void run(Message message, Guild guild, TextChannel channel, User author, String[] args) {
        val subject = String.join(" ", args);

        if (subject.replace(" ", "").isEmpty()) {
            sendError(channel, author, "use `!suporte <assunto>`.", 10);
            message.delete().complete();
            return;
        }

        if (subject.length() > 400) {
            sendError(channel, author, "assunto muito longo.", 10);
            message.delete().complete();
            return;
        }

        try {
            if (!Main.getDb().canCreateTicket(author.getId())) {
                sendError(channel, author, "você não pode criar tickets pois já fez spam.", 10);
                message.delete().complete();
                return;
            }
        } catch (SQLException e) {
            sendError(channel, author, "um erro ocorreu.", 10);
            message.delete().complete();
            e.printStackTrace();
            return;
        }

        try {
            if (!Main.getDb().hasOpenedTicket(author.getId())) {
                sendError(channel, author, "você já tem um ticket aberto.", 10);
                message.delete().complete();
                return;
            }
        } catch (SQLException e) {
            sendError(channel, author, "um erro ocorreu.", 10);
            message.delete().complete();
            e.printStackTrace();
            return;
        }

        val abstractChannel = guild.getController().createTextChannel("ticket-" + author.getId())
                .setParent(guild.getCategoryById(Main.getConfig().getCategoryId()))
                .setTopic(subject).complete();

        val newChannel = guild.getTextChannelById(abstractChannel.getId());
        newChannel.getManager()
                .sync()
                .complete();

        AddCommand.addUser(newChannel, guild.getMember(author));

        try {
            val warnMsg = sendSuccess(channel, author, "aguarde...");
            var ticket = Main.getDb().createReturningTicket(author.getId(), warnMsg.getId(), subject, newChannel.getId());
            newChannel.getManager().setName("\uD83D\uDC9A-ticket-" + ticket.getId()).complete();
            val msg = newChannel.sendMessage(new MessageBuilder(
                    ("\nTicket " + ticket.getId() + "\nAssunto: " + subject +
                            "\nUsuário: " + author.getAsMention()) + "\n\n**Envie aqui fotos, vídeos, prints e perguntas. Quando seu problema estiver resolvido mande `!fechar`**").build()).complete();
            msg.pin().complete();
            warnMsg.editMessage(new MessageBuilder(":white_check_mark: Ticket criado, " + author.getAsMention() + ". Mande suas mensagens em <#" + newChannel.getId() + ">.").build()).complete();
            message.delete().complete();
        } catch (SQLException e) {
            sendError(channel, author, "um erro ocorreu ao tentar criar o ticket.", 10);
            message.delete().complete();
            e.printStackTrace();
        }

    }

}
