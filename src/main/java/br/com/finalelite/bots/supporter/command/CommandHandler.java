package br.com.finalelite.bots.supporter.command;

import br.com.finalelite.bots.supporter.Supporter;
import br.com.finalelite.bots.supporter.utils.SimpleLogger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.var;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class CommandHandler {
    @Getter
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
        val rawContent = message.getContentRaw();
        val supporter = Supporter.getInstance();

        // its with the prefix?
        if (!rawContent.startsWith(prefix)) return false;

        // check if the command exists
        var command = rawContent.substring(prefix.length()).toLowerCase().replace("\n", " \n");
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

        // check if it have a special permission
        if (executedCommand.getPermission() != CommandPermission.EVERYONE) {
            if (executedCommand.getPermission() == CommandPermission.BOT_OWNER) {
                if (!author.getId().equalsIgnoreCase(supporter.getConfig().getOwnerId())) {
                    Command.sendError(textChannel, author, "você não pode usar esse comando.", 10);
                    SimpleLogger.logMessage(textChannel, author, message, "> CODE 4");
                    return false;
                }
            } else {
                val neededRolePosition = supporter.getJda().getRoleById(executedCommand.getPermission().getRoleId()).getPosition();
                if (guild.getMember(author).getRoles().get(0).getPosition() < neededRolePosition) {
                    Command.sendError(textChannel, author, "você não pode usar esse comando.", 10);
                    SimpleLogger.logMessage(textChannel, author, message, "> CODE 1");
                    return false;
                }
            }
        }

        if (!executedCommand.getChecker().canRun(textChannel)) {
            SimpleLogger.logMessage(textChannel, author, message, "> CODE 3");
            return false;
        }

        SimpleLogger.logMessage(textChannel, author, message, "> CODE 0");
        // if passed it all, finally run the command
        executedCommand.run(message, guild, textChannel, author, args);

        return true;
    }

}
