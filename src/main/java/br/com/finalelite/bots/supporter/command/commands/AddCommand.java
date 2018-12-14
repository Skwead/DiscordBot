package br.com.finalelite.bots.supporter.command.commands;

import br.com.finalelite.bots.supporter.command.Command;
import lombok.val;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import java.util.Arrays;

public class AddCommand extends Command {

    public AddCommand() {
        super("add", "adiciona um usuário ao ticket",true, false, true, false, false);
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        if (args.length != 1) {
            sendError(textChannel, author, "use `!add <discord>`.", 10);
            message.delete().complete();
            return;
        }
        val target = message.getMentionedMembers().size() == 1 ? message.getMentionedMembers().get(0) : null;

        if (target == null) {
            sendError(textChannel, author, "use `!add <discord>`.", 10);
            message.delete().complete();
            return;
        }

        addUser(textChannel, target);
    }

    public static void addUser(TextChannel channel, Member user) {
        channel.getManager().putPermissionOverride(user,
                Arrays.asList(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_HISTORY), null).complete();
    }
}
