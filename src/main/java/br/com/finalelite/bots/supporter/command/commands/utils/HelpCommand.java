package br.com.finalelite.bots.supporter.command.commands.utils;

import br.com.finalelite.bots.supporter.Supporter;
import br.com.finalelite.bots.supporter.command.*;
import lombok.val;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HelpCommand extends Command {

    public HelpCommand() {
        super(
                "ajuda",
                "lista os comandos",
                CommandPermission.EVERYONE,
                CommandType.DEFAULT,
                DefaultCommandCategory.UTILS.getCategory()
        );
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        val commandMap = Supporter.getInstance().getCommandHandler().getCommandMap();
        val commands = new HashMap<CommandCategory, List<Command>>();

        commandMap.values().forEach(command -> {
            val category = command.getCategory();
            if (commands.containsKey(category)) {
                val list = commands.get(category);
                list.add(command);
                commands.put(category, list);
            } else {
                val list = new ArrayList<Command>();
                list.add(command);
                commands.put(category, list);
            }
        });

        val embed = new EmbedBuilder()
                .setColor(0xf1c65f)
                .setAuthor("Final Elite", "https://finalelite.com.br", Supporter.getInstance().getJda().getSelfUser().getAvatarUrl())
                .setDescription("Lista de comandos do bot")
                .setFooter(author.getName() + "#" + author.getDiscriminator(), author.getAvatarUrl());

        commands.forEach((category, commandList) -> {
            val sb = new StringBuilder();
            commandList.forEach(command ->
                    sb.append("`").append(Supporter.getInstance().getCommandHandler().getPrefix()).append(command.getName()).append("`: *").append(command.getDescription()).append(".*\n"));

            embed.addField(String.format("%s %s", category.getEmojiName(), category.getName()), sb.toString(), false);
        });

        textChannel.sendMessage(embed.build()).complete();

    }
}
