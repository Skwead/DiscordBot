package br.com.finalelite.bots.supporter.command.commands;

import br.com.finalelite.bots.supporter.Main;
import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.command.CommandPermission;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("ajuda", "lista os comandos", CommandPermission.STAFF, false, true, true, true);
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        val sb = new StringBuilder();
        Main.getCommandHandler().getCommandMap().values().forEach(command ->
                sb.append("\n   - **!").append(command.getName()).append("**: ").append(command.getDescription()));
        sendSuccess(textChannel, author, "Comandos: " + sb.toString());
    }
}
