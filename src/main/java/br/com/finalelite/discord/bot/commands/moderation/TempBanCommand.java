package br.com.finalelite.discord.bot.commands.moderation;

import br.com.finalelite.discord.bot.Bot;
import br.com.finalelite.discord.bot.entity.command.CommandPermission;
import br.com.finalelite.discord.bot.entity.command.TempPunishmentCommand;
import br.com.finalelite.discord.bot.entity.punishment.PunishmentType;
import br.com.finalelite.discord.bot.utils.SimpleLogger;
import lombok.val;

public class TempBanCommand extends TempPunishmentCommand {

    public TempBanCommand() {
        super(
                "tempban",
                "bane um usuário temporáriamente do Discord",
                CommandPermission.MODERATOR,
                PunishmentType.TEMP_BAN
        );

        val jda = Bot.getInstance().getJda();

        SimpleLogger.log("Searching for banned users.");
        Bot.getInstance().getDatabase().getActivePunishmentsByType(PunishmentType.TEMP_BAN).stream()
                .filter(punishment -> punishment.getTarget() != null)
                .forEach(punishment -> {
                    SimpleLogger.log("Found banned user %s, kicking...", punishment.getTarget().getUser().getId());
                    jda.getGuilds().get(0).getController().kick(punishment.getTarget(), punishment.getReason()).complete();
                });
        SimpleLogger.log("Search ended.");
    }

}
