package br.com.finalelite.bots.supporter.command.commands;

import br.com.finalelite.bots.supporter.Main;
import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.CommandType;
import br.com.finalelite.bots.supporter.utils.ConfigManager;
import lombok.val;
import net.dv8tion.jda.core.entities.*;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PresenceCommand extends Command {
    public PresenceCommand() {
        super(
                "jogando",
                "muda a presença do bot",
                CommandPermission.MAJOR_STAFF,
                CommandType.STAFF
        );
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        val types = Arrays.stream(Game.GameType.values()).map(Game.GameType::name).collect(Collectors.toList());
        if (args.length < 3) {
            sendError(textChannel, author, "use `!jogando <" + String.join("/", types) + "> <URL> <título>`.");
            return;
        }

        try {
            val type = Game.GameType.valueOf(args[0].toUpperCase());
            val url = args[1];
            val label = Arrays.stream(args).skip(2).collect(Collectors.joining(" ")).replace("`", "");

            val config = Main.getConfig();
            config.getPresence().setLabel(label);
            config.getPresence().setType(type);
            config.getPresence().setUrl(url);

            Main.getJda().getPresence().setGame(config.getPresence().toGame());

            ConfigManager.saveConfigToFile(config);
            Main.loadConfig();

            sendSuccess(textChannel, author, "presença alterada.");
        } catch (IllegalArgumentException e) {
            sendError(textChannel, author, "tipo inválido. Use `!jogando <" + String.join("/", types) + "> <URL> <título>`.");
        }
    }
}
