package br.com.finalelite.discord.bot.commands.utils;

import br.com.finalelite.discord.bot.entity.command.CommandBase;
import br.com.finalelite.discord.bot.entity.command.CommandChannelChecker;
import br.com.finalelite.discord.bot.entity.command.CommandPermission;
import br.com.finalelite.discord.bot.entity.command.DefaultCommandCategory;
import lombok.var;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.stream.Collectors;

public class ClearCommand extends CommandBase {

    public ClearCommand() {
        super(
                "clear",
                "deleta mensagens",
                CommandPermission.MODERATOR,
                CommandChannelChecker.DISABLE,
                DefaultCommandCategory.UTILS
        );
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        if (args.length == 0 || args.length > 3) {
            sendError(textChannel, author, "use `!clear <quantidade> [<usuário ou canal>] [<usuário ou canal>]`.");
            return;
        }

        var amount = 99;
        try {
            amount = Math.min(amount, Integer.parseInt(args[0]));
            if (amount <= 1) {
                sendError(textChannel, author, "a quantidade deve ser maior do que 1.");
                return;
            }
        } catch (NumberFormatException e) {
            sendError(textChannel, author, "use `!clear <quantidade> [<usuário ou canal>] [<usuário ou canal>]`.");
            return;
        }

        var channel = textChannel;
        @SuppressWarnings("RedundantCast")
        var user = (User) null;
        if (message.getMentionedChannels().size() == 1)
            channel = message.getMentionedChannels().get(0);
        else if (message.getMentionedChannels().size() > 1) {
            sendError(textChannel, author, "mencione apenas um usuário e/ou um canal.");
            return;
        }

        if (message.getMentionedUsers().size() == 1)
            user = message.getMentionedUsers().get(0);
        else if (message.getMentionedUsers().size() > 1) {
            sendError(textChannel, author, "mencione apenas um usuário e/ou um canal.");
            return;
        }

        // add one more message (the one that the author sent)
        if (channel == textChannel)
            amount++;

        var messages = channel.getIterableHistory().limit(amount).complete();

        if (user != null) {
            var finalUser = user;
            messages = messages.stream().filter(msg -> msg.getAuthor().getId().equalsIgnoreCase(finalUser.getId())).collect(Collectors.toList());
        }

        try {
            if (!messages.isEmpty())
                channel.deleteMessages(messages).complete();
        } catch (Exception e) {
            sendError(textChannel, author, "algo deu erro. Não é possível deletar mensagens com mais de 2 semanas.");
        }

        sendSuccess(channel, author,
                String.format("%d mensagens%s deletadas por %s.", messages.size(), (user == null ? "" : " de " + user.getAsMention()), author.getAsMention()),
                10);
    }
}
