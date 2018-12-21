package br.com.finalelite.bots.supporter.command.commands;

import br.com.finalelite.bots.supporter.Supporter;
import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.command.CommandPermission;
import lombok.val;
import lombok.var;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class SupportCommand extends Command {
    public SupportCommand() {
        super("suporte", "cria um novo ticket", CommandPermission.EVERYONE, true, false, false, false);
    }

    @Override
    public void run(Message message, Guild guild, TextChannel channel, User author, String[] args) {
        val subject = String.join(" ", args);
        val supporter = Supporter.getInstance();

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

        if (!supporter.getInstance().getDatabase().canCreateTicket(author.getId())) {
            sendError(channel, author, "você não pode criar tickets pois já fez spam.", 10);
            message.delete().complete();
            return;
        }

        if (!supporter.getDatabase().hasOpenedTicket(author.getId())) {
            sendError(channel, author, "você já tem um ticket aberto.", 10);
            message.delete().complete();
            return;
        }

        val abstractChannel = guild.getController().createTextChannel("ticket-" + author.getId())
                .setParent(guild.getCategoryById(supporter.getConfig().getCategoryId()))
                .setTopic(subject).complete();

        val newChannel = guild.getTextChannelById(abstractChannel.getId());
        newChannel.getManager()
                .sync()
                .complete();

        AddCommand.addUser(newChannel, guild.getMember(author));

        val warnMsg = sendSuccess(channel, author, "aguarde...");
        var ticket = supporter.getDatabase().createReturningTicket(author.getId(), warnMsg.getId(), subject, newChannel.getId());
        newChannel.getManager().setName("\uD83D\uDC9A-ticket-" + ticket.getId()).complete();
        val msg = newChannel.sendMessage(new MessageBuilder(
                ("\nTicket " + ticket.getId() + "\nAssunto: " + subject +
                        "\nUsuário: " + author.getAsMention()) + "\n\n**Envie aqui fotos, vídeos, prints e perguntas. Quando seu problema estiver resolvido mande `!fechar`**").build()).complete();
        msg.pin().complete();
        warnMsg.editMessage(new MessageBuilder(":white_check_mark: Ticket criado, " + author.getAsMention() + ". Mande suas mensagens em <#" + newChannel.getId() + ">.").build()).complete();
        message.delete().complete();

    }

}
