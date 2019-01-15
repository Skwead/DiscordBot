package br.com.finalelite.bots.supporter.command.commands.moderation;

import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.CommandChannelChecker;
import br.com.finalelite.bots.supporter.command.commands.moderation.utils.ModerationUtils;
import br.com.finalelite.bots.supporter.command.commands.moderation.utils.Punishment;
import br.com.finalelite.bots.supporter.command.commands.moderation.utils.PunishmentCommand;
import br.com.finalelite.bots.supporter.command.commands.moderation.utils.PunishmentType;
import br.com.finalelite.bots.supporter.utils.SimpleLogger;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

import java.util.Date;

public class TempBanCommand extends PunishmentCommand {

    public TempBanCommand() {
        super(
                "tempban",
                "bane um usuário temporáriamente do Discord",
                CommandPermission.MAJOR_STAFF,
                CommandChannelChecker.DISABLE
        );
    }

    @Override
    public boolean runCommand(Guild guild, Member author, Member target, String reason) {
        try {
            val punishment = Punishment.builder()
                    .author(author)
                    .relatedGuild(guild)
                    .type(PunishmentType.TEMP_BAN)
                    .date(new Date())
                    .end(null)
                    .reason(reason)
                    .target(target);

            ModerationUtils.apply(punishment.build());
            return true;
        } catch (Exception e) {
            if (e.getMessage() == null || !e.getMessage().equals("Can't modify a member with higher or equal highest role than yourself!")) {
                e.printStackTrace();
                SimpleLogger.sendStackTraceToOwner(e);
            }
        }
        return false;
    }
}
