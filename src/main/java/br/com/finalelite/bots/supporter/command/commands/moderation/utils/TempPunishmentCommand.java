package br.com.finalelite.bots.supporter.command.commands.moderation.utils;

import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.CommandChannelChecker;
import br.com.finalelite.bots.supporter.command.DefaultCommandCategory;
import net.dv8tion.jda.core.entities.*;

public abstract class TempPunishmentCommand extends Command {
    public TempPunishmentCommand(String name, String description, CommandPermission permission, CommandChannelChecker type) {
        super(name, description, permission, type, DefaultCommandCategory.MODERATION.getCategory());
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        if (message.getMentionedUsers().size() != 1 || args.length < 1 || !args[0].equals(message.getMentionedUsers().get(0).getAsMention())) {
            sendError(textChannel, author, "use `!" + getName() + " <usuário> <tempo> <unidade de tempo> [<motivo>]`.", 30);
            return;
        }

    }

    public abstract boolean runCommand(Guild guild, Member author, Member target, String reason);
}
