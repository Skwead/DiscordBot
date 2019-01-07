package br.com.finalelite.bots.supporter.command;

import br.com.finalelite.bots.supporter.Supporter;
import br.com.finalelite.bots.supporter.utils.SimpleLogger;
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

    public Map<String, Command> getCommandMap() {
        return commands;
    }

    public boolean handle(MessageReceivedEvent event) {
        val message = event.getMessage();
        val author = event.getAuthor();
        val guild = event.getGuild();
        val textChannel = event.getTextChannel();
        val parent = textChannel.getParent();
        val rawContent = message.getContentRaw();
        val supporter = Supporter.getInstance();

        // its with the prefix?
        if (!rawContent.startsWith(prefix)) return false;

        // check if the command exists
        var command = rawContent.substring(prefix.length()).toLowerCase();
        if (command.contains(" "))
            command = command.substring(0, command.indexOf(" "));

        if (!commands.containsKey(command))
            return false;

        // prepare the arguments
        var rawArgs = rawContent.substring(prefix.length() + command.length()).replaceAll("\\s+", " ");
        var args = new String[0];
        if (rawArgs.contains(" "))
            args = rawArgs.substring(1).split(" ");

        // prepare to execute the command
        val executedCommand = commands.get(command);
        // check if its staff only
        if (executedCommand.getPermission() == CommandPermission.STAFF && !(guild.getMemberById(author.getId()).getRoles().contains(guild.getRoleById(supporter.getConfig().getStaffRoleId())))) {
            Command.sendError(textChannel, author, "você não pode usar esse comando.", 10);
            SimpleLogger.logMessage(textChannel, author, message, "CODE 1");
            return false;
        }

        // check if its major staff
        if (executedCommand.getPermission() == CommandPermission.MAJOR_STAFF && !(guild.getMemberById(author.getId()).getRoles().contains(guild.getRoleById(supporter.getConfig().getAdminRoleId())))) {
            Command.sendError(textChannel, author, "você não pode usar esse comando.", 10);
            SimpleLogger.logMessage(textChannel, author, message, "CODE 2");
            return false;
        }

        if (!executedCommand.getType().isDisableChannelCheck()) {
            // check if its usable in staff channel
            if (textChannel.getId().equals(supporter.getConfig().getStaffChannelId()) && !executedCommand.getType().isUsableInStaffChannel()) {
                SimpleLogger.logMessage(textChannel, author, message, "CODE 3");
                return false;
            }

            // check if its usable in support channel
            if (textChannel.getId().equals(supporter.getConfig().getSupportChannelId()) && !executedCommand.getType().isUsableInSupportChannel()) {
                SimpleLogger.logMessage(textChannel, author, message, "CODE 4");
                return false;
            }

            // check if its usable in the main category
            if (parent.getId().equals(supporter.getConfig().getSupportCategoryId()) && !executedCommand.getType().isUsableInOpenedCategory()) {
                SimpleLogger.logMessage(textChannel, author, message, "CODE 5");
                return false;
            }

            // check if its usable in closed category
            if (parent.getId().equals(supporter.getConfig().getClosedCategoryId()) && !executedCommand.getType().isUsableInClosedCategory()) {
                SimpleLogger.logMessage(textChannel, author, message, "CODE 6");
                return false;
            }
        }

        SimpleLogger.logMessage(textChannel, author, message, "CODE 0");
        // if passed it all, finally run the command
        executedCommand.run(message, guild, textChannel, author, args);

        return true;
    }

}
