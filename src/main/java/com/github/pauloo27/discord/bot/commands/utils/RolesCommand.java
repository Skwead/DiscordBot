package com.github.pauloo27.discord.bot.commands.utils;

import com.github.pauloo27.discord.bot.entity.command.CommandBase;
import com.github.pauloo27.discord.bot.entity.command.CommandChannelChecker;
import com.github.pauloo27.discord.bot.entity.command.CommandPermission;
import com.github.pauloo27.discord.bot.entity.command.DefaultCommandCategory;
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
