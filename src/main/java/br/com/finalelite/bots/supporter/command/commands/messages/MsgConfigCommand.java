package br.com.finalelite.bots.supporter.command.commands.messages;

import br.com.finalelite.bots.supporter.Main;
import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.utils.ConfigManager;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class MsgConfigCommand extends Command {

    public MsgConfigCommand() {
        super("msgconfig", true, false, false, false, true);
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        if (args.length < 2) {
            sendError(textChannel, author, "use `/!msgconfig <add/remover/editar/ph> [<atalho>] [<mensagem>]`.");
            message.delete().complete();
            return;
        }

        val action = args[0];
        val shortcut = args[1];

        if (!(action.equalsIgnoreCase("remover") || action.equalsIgnoreCase("editar") || action.equalsIgnoreCase("add"))) {
            sendError(textChannel, author, "use `/!msgconfig <add/remover/editar> <atalho> [<mensagem>]`.");
            message.delete().complete();
            return;
        }

        if (action.equalsIgnoreCase("remover")) {
            val config = Main.getConfig();
            config.getMessages().remove(shortcut);
            ConfigManager.saveConfigToFile(config);
            sendSuccess(textChannel, author, String.format("mensagem `%s` removida com sucesso.", shortcut));
            return;
        }

        if (args.length < 3) {
            sendError(textChannel, author, String.format("use `/!msgconfig <%s> <atalho> <mensagem>`.", action));
            message.delete().complete();
            return;
        }

        val text = args[2];

        if (action.equalsIgnoreCase("add")) {
            val config = Main.getConfig();
            if (config.getMessages().containsKey(shortcut)) {
                message.delete().complete();
                sendError(textChannel, author, "uma mensagem já existe com o atalho `%s`, Use `!msgconfig editar` para editar ou `!msgconfig remover` para apagar.");
                return;
            }

            config.getMessages().put(shortcut, text);
            ConfigManager.saveConfigToFile(config);
            sendSuccess(textChannel, author, String.format("mensagem `%s` adicionada com sucesso.", shortcut));
            return;
        }

        if (action.equalsIgnoreCase("editar")) {
            val config = Main.getConfig();
            if (!config.getMessages().containsKey(shortcut)) {
                message.delete().complete();
                sendError(textChannel, author, "não foi encontrada uma mensagem com o atalho`%s`. Use `!msgconfig add` para criar uma.");
                return;
            }

            config.getMessages().put(shortcut, text);
            ConfigManager.saveConfigToFile(config);
            sendSuccess(textChannel, author, String.format("mensagem `%s` editada com sucesso.", shortcut));
        }
    }
}
