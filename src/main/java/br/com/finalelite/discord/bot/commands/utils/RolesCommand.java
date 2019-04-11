package br.com.finalelite.discord.bot.commands.utils;

import br.com.finalelite.discord.bot.entity.command.CommandBase;
import br.com.finalelite.discord.bot.entity.command.CommandChannelChecker;
import br.com.finalelite.discord.bot.entity.command.CommandPermission;
import br.com.finalelite.discord.bot.entity.command.DefaultCommandCategory;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class RolesCommand extends CommandBase {
    public RolesCommand() {
        super(
                "cargos",
                "lista os cargos e seus IDs",
                CommandPermission.MANAGER,
                CommandChannelChecker.DISABLE,
                DefaultCommandCategory.UTILS
        );
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        StringBuilder sb = new StringBuilder("Lista dos cargos e seus IDs: \n");
        guild.getRoles().forEach(role -> sb.append("`").append(role.getName()).append("`").append(" > `").append(role.getId()).append("`\n"));
        sendSuccess(textChannel, author, sb.toString());
    }
}
