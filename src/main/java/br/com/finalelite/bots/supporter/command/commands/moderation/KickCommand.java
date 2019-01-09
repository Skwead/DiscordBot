package br.com.finalelite.bots.supporter.command.commands.moderation;

import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.CommandType;
import br.com.finalelite.bots.supporter.command.DefaultCommandCategory;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.Date;

public class KickCommand extends Command {

    public KickCommand() {
        super(
                "kick",
                "expulsa um usuário do Discord",
                CommandPermission.STAFF,
                CommandType.DEFAULT,
                DefaultCommandCategory.MODERATION.getCategory()
        );
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        if (args.length != 1) {
            sendError(textChannel, author, "use `!kick <usuário>`.", 20);
            return;
        }

        ModerationUtils.logModeration(
                new Punishment(1, new Date(), guild.getMember(author), guild.getMember(author), PunishmentType.KICK, "Testing", new Date(), false));

        val user = message.getMentionedUsers().get(0);
        try {
            guild.getController().kick(guild.getMember(user)).complete();
            sendSuccess(textChannel, author, " usuário " + user.getAsMention() + "kickado com sucesso.");
        } catch (Exception e) {
            sendError(textChannel, author, "não foi possível kickar esse usuário, talvez eu não tenha permissão para kicka-lo.", 20);
        }
    }
}
