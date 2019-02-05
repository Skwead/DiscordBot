package br.com.finalelite.bots.supporter.command.commands.utils;

import br.com.finalelite.bots.supporter.Supporter;
import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.command.CommandChannelChecker;
import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.DefaultCommandCategory;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class PingCommand extends Command {
    public PingCommand() {
        super(
                "ping",
                "retorna a latência do bot",
                CommandPermission.STAFF,
                CommandChannelChecker.DISABLE,
                DefaultCommandCategory.UTILS
        );
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        sendSuccess(textChannel, author, String.format("o meu ping é %d.", Supporter.getInstance().getJda().getPing()));
        message.delete().complete();
    }
}
