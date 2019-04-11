package br.com.finalelite.discord.bot.entity.command;

import br.com.finalelite.discord.bot.Bot;
import br.com.finalelite.discord.bot.entity.punishment.PunishmentType;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public abstract class RevertPunishmentCommandBase extends CommandBase {

    private PunishmentType[] types;

    public RevertPunishmentCommandBase(String name, String description, CommandPermission permission, PunishmentType... types) {
        super(name, description, permission, CommandChannelChecker.DISABLE, DefaultCommandCategory.MODERATION);
        this.types = types;
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        if (message.getMentionedUsers().size() < 1 || args.length != 1 || !args[0].equals(message.getMentionedUsers().get(0).getAsMention())) {
            if (args.length == 0 || !args[0].matches("[0-9]+")) {
                sendError(textChannel, author, "use `!" + getName() + " <usuário>`.", 30);
                return;
            }
        }

        val userId = args[0].matches("[0-9]+") ? args[0] : null;
        val user = userId == null ? message.getMentionedUsers().get(0) : null;
        val punishment = Bot.getInstance().getDatabase().getActivePunishmentByUser(userId == null ? user.getId() : userId, types);
        if (punishment != null) {
            Bot.getInstance().getPunishmentManager().revert(punishment);
            sendSuccess(textChannel, author, "usuário despunido.", 40);
        } else
            sendError(textChannel, author, "usuário não encontra-se punido.", 30);
    }

}
