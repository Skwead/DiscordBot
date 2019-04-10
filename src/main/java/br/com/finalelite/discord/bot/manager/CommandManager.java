package br.com.finalelite.discord.bot.manager;

import br.com.finalelite.discord.bot.Bot;
import br.com.finalelite.discord.bot.entity.command.Command;
import br.com.finalelite.discord.bot.entity.command.CommandPermission;
import br.com.finalelite.discord.bot.utils.DiscordUtils;
import br.com.finalelite.discord.bot.utils.SimpleLogger;
import lombok.Getter;
import lombok.val;
import lombok.var;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    @Getter
    private final String prefix;
    private Map<String, Command> commands = new HashMap<>();

    public CommandManager(String prefix) {
        this.prefix = prefix;
        Bot.getInstance().getJda().addEventListener(new MessageListener());
    }

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
        val supporter = Bot.getInstance();

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
                    DiscordUtils.sendError(textChannel, author, "você não pode usar esse comando.", 10);
                    SimpleLogger.logMessage(textChannel, author, message, "> CODE 4");
                    return false;
                }
            } else {
                val neededRolePosition = supporter.getJda().getRoleById(executedCommand.getPermission().getRoleId()).getPosition();
                if (guild.getMember(author).getRoles().get(0).getPosition() < neededRolePosition) {
                    DiscordUtils.sendError(textChannel, author, "você não pode usar esse comando.", 10);
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

    private class MessageListener extends ListenerAdapter {
        @Override
        public void onMessageReceived(MessageReceivedEvent event) {
            val message = event.getMessage();
            val channel = message.getChannel();
            val author = message.getAuthor();
            val config = Bot.getInstance().getConfig();

            if (author.isBot())
                return; // limit the "friends type" hoho

            if (channel.getType() == ChannelType.PRIVATE) {
                // lets disable message in the DM
                channel.sendMessage(String.format("Não respondo via DM ainda, utilize o chat <#%s> para executar os comandos.", config.getSupportChannelId())).complete();
                return;
            }

            // okay, lets handle the command. If this is a invalid command and it's executed in the support channel, delete this spam message
            if (!handle(event) && channel.getId().equals(config.getSupportChannelId())) {
                message.delete().complete();
            }
        }
    }

}
