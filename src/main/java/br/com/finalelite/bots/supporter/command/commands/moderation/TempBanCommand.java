package br.com.finalelite.bots.supporter.command.commands.moderation;

import br.com.finalelite.bots.supporter.Supporter;
import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.commands.moderation.utils.PunishmentType;
import br.com.finalelite.bots.supporter.command.commands.moderation.utils.TempPunishmentCommand;
import br.com.finalelite.bots.supporter.utils.SimpleLogger;
import lombok.val;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class TempBanCommand extends TempPunishmentCommand {

    public TempBanCommand() {
        super(
                PunishmentType.TEMP_BAN,
                "tempban",
                "bane um usuário temporáriamente do Discord",
                CommandPermission.MAJOR_STAFF
        );

        val jda = Supporter.getInstance().getJda();
        jda.addEventListener(new TempBanListener());

        SimpleLogger.log("Searching for banned users.");
        Supporter.getInstance().getDatabase().getActivateBans().stream()
                .filter(punishment -> punishment.getTarget() != null)
                .forEach(punishment -> {
                    SimpleLogger.log("Found banned user %s, kicking...", punishment.getTarget().getUser().getId());
                    jda.getGuilds().get(0).getController().kick(punishment.getTarget(), punishment.getReason()).complete();
                });
        SimpleLogger.log("Search ended.");
    }

    public class TempBanListener extends ListenerAdapter {
        @Override
        public void onGuildMemberJoin(GuildMemberJoinEvent event) {
            val user = event.getUser();
            val reason = Supporter.getInstance().getDatabase().getTempBanReasonOrNull(event.getUser().getId());
            if (reason != null) {
                System.out.printf("%s#%s (%s)  did an ooopsie: %s%n", user.getName(), user.getDiscriminator(), user.getId(), reason);
                event.getGuild().getController().kick(event.getGuild().getMember(user), reason).complete();
            }
        }
    }

}
