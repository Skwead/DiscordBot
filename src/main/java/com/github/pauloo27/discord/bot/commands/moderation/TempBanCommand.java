package com.github.pauloo27.discord.bot.commands.moderation;

import com.github.pauloo27.discord.bot.Bot;
import com.github.pauloo27.discord.bot.entity.command.CommandPermission;
import com.github.pauloo27.discord.bot.entity.command.TempPunishmentCommandBase;
import com.github.pauloo27.discord.bot.entity.punishment.PunishmentType;
import com.github.pauloo27.discord.bot.utils.SimpleLogger;
import lombok.val;

public class TempBanCommand extends TempPunishmentCommandBase {

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
