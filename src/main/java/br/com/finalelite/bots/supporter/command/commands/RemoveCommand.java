package br.com.finalelite.bots.supporter.command.commands;

import br.com.finalelite.bots.supporter.command.Command;
import lombok.val;
import net.dv8tion.jda.core.entities.*;

public class RemoveCommand extends Command {

    public RemoveCommand() {
        super("remover", true, false, true, false, false);
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        if (args.length != 1) {
            sendError(textChannel, author, "use `!remover <discord>`.", 10);
            message.delete().complete();
            return;
        }
        val target = message.getMentionedMembers().size() == 1 ? message.getMentionedMembers().get(0) : null;

        if (target == null) {
            sendError(textChannel, author, "use `!remover <discord>`.", 10);
            message.delete().complete();
            return;
        }

        removeUser(textChannel, target);
    }

    public static void removeUser(TextChannel channel, Member user) {
        channel.getManager().removePermissionOverride(user).complete();
    }
}
