package br.com.finalelite.discord.bot.command.commands.utils;

import br.com.finalelite.discord.bot.Bot;
import br.com.finalelite.discord.bot.command.Command;
import br.com.finalelite.discord.bot.command.CommandChannelChecker;
import br.com.finalelite.discord.bot.command.CommandPermission;
import br.com.finalelite.discord.bot.command.DefaultCommandCategory;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class PingCommand extends Command {
    public PingCommand() {
        super(
                "ping",
                "retorna a latência do bot",
                CommandPermission.BOT_OWNER,
                CommandChannelChecker.DISABLE,
                DefaultCommandCategory.UTILS
        );
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        sendSuccess(textChannel, author, String.format("o meu ping é %d.", Bot.getInstance().getJda().getPing()));
        message.delete().complete();
    }
}
