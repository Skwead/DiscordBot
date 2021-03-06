package com.github.pauloo27.discord.bot.commands.support;

import com.github.pauloo27.discord.bot.Bot;
import com.github.pauloo27.discord.bot.entity.command.CommandBase;
import com.github.pauloo27.discord.bot.entity.command.CommandChannelChecker;
import com.github.pauloo27.discord.bot.entity.command.CommandPermission;
import com.github.pauloo27.discord.bot.entity.command.DefaultCommandCategory;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.Arrays;
import java.util.stream.Collectors;

public class MsgConfigCommand extends CommandBase {

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
            Bot.getInstance().getConfigManager().reloadConfig(config);
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
            Bot.getInstance().getConfigManager().reloadConfig(config);
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
            Bot.getInstance().getConfigManager().reloadConfig(config);
            sendSuccess(textChannel, author, String.format("mensagem `%s` editada com sucesso.", shortcut));
        }
    }
}
