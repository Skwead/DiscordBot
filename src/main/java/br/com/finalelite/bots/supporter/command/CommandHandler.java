package br.com.finalelite.bots.supporter.command;

import br.com.finalelite.bots.supporter.Main;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.var;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class CommandHandler {
    private final String prefix;

    private Map<String, Command> commands = new HashMap<>();

    public void registerCommand(Command command) {
        commands.put(command.getName().toLowerCase(), command);
    }

    public boolean handle(MessageReceivedEvent event) {
        val message = event.getMessage();
        val author = event.getAuthor();
        val guild = event.getGuild();
        val textChannel = event.getTextChannel();
        val parent = textChannel.getParent();
        val rawContent = message.getContentRaw();

        if (!rawContent.startsWith(prefix)) return false;

        var command = rawContent.substring(prefix.length()).toLowerCase();
        if (command.contains(" "))
            command = command.substring(0, command.indexOf(" "));
        if (!commands.containsKey(command))
            return false;

        var rawArgs = rawContent.substring(prefix.length() + command.length()).replaceAll("\\s+", " ");
        var args = new String[0];
        if (rawArgs.contains(" "))
            args = rawArgs.substring(1).split(" ");

        val executedCommand = commands.get(command);
        if (executedCommand.isStaffOnly() && !(guild.getMemberById(author.getId()).getRoles().contains(guild.getRoleById(Main.getConfig().getStaffRoleId())))) {
            executedCommand.sendError(textChannel, author, "você não pode usar esse comando.", 10);
            return false;
        }

        if (textChannel.getId().equals(Main.getConfig().getStaffChannelId()) && !executedCommand.isUsableInStaffChannel()) {
            return false;
        }

        if (textChannel.getId().equals(Main.getConfig().getSupportChannelId()) && !executedCommand.isUsableInSupportChannel()) {
            return false;
        }

        if (parent.getId().equals(Main.getConfig().getCategoryId()) && !executedCommand.isUsableInOpenedCategory()) {
            return false;
        }

        if (parent.getId().equals(Main.getConfig().getClosedCategoryId()) && !executedCommand.isUsableInClosedCategory()) {
            return false;
        }

        executedCommand.run(message, guild, textChannel, author, args);

        return true;
    }

}
