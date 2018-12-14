package br.com.finalelite.bots.supporter.command.commands;

import br.com.finalelite.bots.supporter.Main;
import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.utils.ConfigManager;
import lombok.val;
import net.dv8tion.jda.core.entities.*;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PrecenseCommand extends Command {
    public PrecenseCommand() {
        super("jogando", "muda a presença do bot", true, false, false, false, true);
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        val types = Arrays.stream(Game.GameType.values()).map(Game.GameType::name).collect(Collectors.toList());
        if (args.length != 3) {
            sendError(textChannel, author, "use `!jogando <" + String.join("/", types) + "> <título> <URL>`.");
            return;
        }

        try {
            val type = Game.GameType.valueOf(args[0].toUpperCase());
            val label = args[1];
            val url = args[2];

            val config = Main.getConfig();
            config.getPresence().setLabel(label);
            config.getPresence().setType(type);
            config.getPresence().setUrl(url);

            Main.getJda().getPresence().setGame(config.getPresence().toGame());

            ConfigManager.saveConfigToFile(config);
            Main.loadConfig();

            sendSuccess(textChannel, author, "presença alterada.");
        } catch (IllegalArgumentException e) {
            sendError(textChannel, author, "tipo inválido. Use `!jogando <" + String.join("/", types) + "> <título> <URL>`.");
        }
    }
}
