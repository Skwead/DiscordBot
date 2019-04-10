package br.com.finalelite.discord.bot.command.commands.support;

import br.com.finalelite.discord.bot.Bot;
import br.com.finalelite.discord.bot.command.Command;
import br.com.finalelite.discord.bot.command.CommandChannelChecker;
import br.com.finalelite.discord.bot.command.CommandPermission;
import br.com.finalelite.discord.bot.command.DefaultCommandCategory;
import br.com.finalelite.discord.bot.utils.ConfigManager;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.Arrays;
import java.util.stream.Collectors;

public class MsgConfigCommand extends Command {

    public MsgConfigCommand() {
        super(
                "msgconfig",
                "configura as mensagens pre-definidas",
                CommandPermission.ADMIN,
                CommandChannelChecker.STAFF,
                DefaultCommandCategory.SUPPORT
        );
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        if (args.length < 2) {
            sendError(textChannel, author, "use `/!msgconfig <add/remover/editar> [<atalho>] [<mensagem>]`.");
            message.delete().complete();
            return;
        }

        val action = args[0];
        val shortcut = args[1];

        if (!(action.equalsIgnoreCase("remover") || action.equalsIgnoreCase("editar") || action.equalsIgnoreCase("add"))) {
            sendError(textChannel, author, "use `!msgconfig <add/remover/editar> <atalho> [<mensagem>]`.");
            message.delete().complete();
            return;
        }

        if (action.equalsIgnoreCase("remover")) {
            val config = Bot.getInstance().getConfig();
            config.getMessages().remove(shortcut);
            ConfigManager.saveConfigToFile(config);
            sendSuccess(textChannel, author, String.format("mensagem `%s` removida com sucesso.", shortcut));
            return;
        }

        if (args.length < 3) {
            sendError(textChannel, author, String.format("use `!msgconfig %s <atalho> <mensagem>`.", action));
            message.delete().complete();
            return;
        }

        val text = Arrays.stream(args).skip(2).collect(Collectors.joining(" ")).replace("\\n", "\n");

        if (action.equalsIgnoreCase("add")) {
            val config = Bot.getInstance().getConfig();
            if (config.getMessages().containsKey(shortcut)) {
                message.delete().complete();
                sendError(textChannel, author, String.format("uma mensagem já existe com o atalho `%s`, Use `!msgconfig editar` para editar ou `!msgconfig remover` para apagar.", shortcut));
                return;
            }

            config.getMessages().put(shortcut, text);
            ConfigManager.saveConfigToFile(config);
            sendSuccess(textChannel, author, String.format("mensagem `%s` adicionada com sucesso.", shortcut));
            return;
        }

        if (action.equalsIgnoreCase("editar")) {
            val config = Bot.getInstance().getConfig();
            if (!config.getMessages().containsKey(shortcut)) {
                message.delete().complete();
                sendError(textChannel, author, String.format("não foi encontrada uma mensagem com o atalho `%s`. Use `!msgconfig add` para criar uma.", shortcut));
                return;
            }

            config.getMessages().put(shortcut, text);
            ConfigManager.saveConfigToFile(config);
            sendSuccess(textChannel, author, String.format("mensagem `%s` editada com sucesso.", shortcut));
        }
    }
}
