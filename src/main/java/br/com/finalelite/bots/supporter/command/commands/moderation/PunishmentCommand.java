package br.com.finalelite.bots.supporter.command.commands.moderation;

import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.CommandType;
import br.com.finalelite.bots.supporter.command.DefaultCommandCategory;
import lombok.val;
import lombok.var;
import net.dv8tion.jda.core.entities.*;

import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class PunishmentCommand extends Command {
    public PunishmentCommand(String name, String description, CommandPermission permission, CommandType type) {
        super(name, description, permission, type, DefaultCommandCategory.MODERATION.getCategory());
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        if (message.getMentionedUsers().size() != 1 || args.length < 1 || !args[0].equals(message.getMentionedUsers().get(0).getAsMention())) {
            sendError(textChannel, author, "use `!" + getName() + " <usuário> [<motivo>]`.", 30);
            return;
        }

        val user = message.getMentionedUsers().get(0);
        var reason = "Nenhum motivo mencionado";
        if (args.length >= 2)
            reason = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));

        if (runCommand(guild, guild.getMember(author), guild.getMember(user), reason))
            sendSuccess(textChannel, author, " usuário " + user.getAsMention() + " kickado com sucesso.");
        else
            sendError(textChannel, author, "não foi possível kickar esse usuário, talvez eu não tenha permissão para kicka-lo.", 30);

    }

    public abstract boolean runCommand(Guild guild, Member author, Member target, String reason);
}
