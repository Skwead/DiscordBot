package br.com.finalelite.bots.supporter.command.commands.utils;

import br.com.finalelite.bots.supporter.Supporter;
import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.command.CommandChannelChecker;
import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.DefaultCommandCategory;
import br.com.finalelite.bots.supporter.utils.SimpleLogger;
import lombok.val;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HelpCommand extends Command {

    private final static String everyoneEmoji = "<:yeap:531458963770966016>";
    private final static String staffEmoji = ":hammer_pick:";

    public HelpCommand() {
        super(
                "ajuda",
                "lista os comandos",
                CommandPermission.EVERYONE,
                CommandChannelChecker.DISABLE,
                DefaultCommandCategory.UTILS
        );
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        val commandMap = Supporter.getInstance().getCommandHandler().getCommandMap();
        val commands = new HashMap<DefaultCommandCategory, List<Command>>();

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
                .setDescription("**Lista de comandos do bot**\n" +
                        everyoneEmoji + ": Comando para todos.\n" +
                        staffEmoji + ": Comando para a equipe.\n")
                .setFooter(author.getName() + "#" + author.getDiscriminator(), author.getAvatarUrl());

        commands.forEach((category, commandList) -> {
            val sb = new StringBuilder();
            commandList.forEach(command ->
                    sb.append(command.getPermission() == CommandPermission.EVERYONE ? everyoneEmoji : staffEmoji).append(" `").append(Supporter.getInstance().getCommandHandler().getPrefix()).append(command.getName()).append("`: **").append(command.getDescription()).append(".**\n"));

            embed.addBlankField(false);
            embed.addField(String.format("\n%s **%s**\n", category.getEmojiName(), category.getName()), sb.toString(), false);
        });

        if (embed.isValidLength(AccountType.BOT))
            textChannel.sendMessage(embed.build()).complete();
        else
            SimpleLogger.sendLogToOwner("The help message is too long! It's time to create a better help command.");
    }
}
