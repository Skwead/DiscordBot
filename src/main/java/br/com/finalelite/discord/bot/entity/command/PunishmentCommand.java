package br.com.finalelite.discord.bot.entity.command;

import br.com.finalelite.discord.bot.Bot;
import br.com.finalelite.discord.bot.entity.command.Command;
import br.com.finalelite.discord.bot.entity.command.CommandChannelChecker;
import br.com.finalelite.discord.bot.entity.command.CommandPermission;
import br.com.finalelite.discord.bot.entity.command.DefaultCommandCategory;
import br.com.finalelite.discord.bot.manager.PunishmentManager;
import br.com.finalelite.discord.bot.entity.punishment.Punishment;
import br.com.finalelite.discord.bot.entity.punishment.PunishmentType;
import br.com.finalelite.discord.bot.utils.SimpleLogger;
import lombok.val;
import lombok.var;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

public abstract class PunishmentCommand extends Command {

    private PunishmentType type;

    public PunishmentCommand(String name, String description, CommandPermission permission, PunishmentType type) {
        super(name, description, permission, CommandChannelChecker.DISABLE, DefaultCommandCategory.MODERATION);
        this.type = type;
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        if (message.getMentionedUsers().size() < 1 || args.length < 1 || !args[0].equals(message.getMentionedUsers().get(0).getAsMention())) {
            sendError(textChannel, author, "use `!" + getName() + " <usuário> [<motivo>]`.", 30);
            return;
        }

        val user = message.getMentionedUsers().get(0);
        var reason = "Nenhum motivo mencionado";
        if (args.length >= 2)
            reason = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));

        val now = new Date();
        try {
            val punishment = Punishment.builder()
                    .authorId(author.getId())
                    .relatedGuildId(guild.getId())
                    .type(type)
                    .dateSeconds(Punishment.parseDate(now))
                    .endSeconds(Punishment.parseDate(getDefaultEndDate(now)))
                    .reason(reason)
                    .targetId(user.getId());

            Bot.getInstance().getPunishmentManager().apply(punishment.build());
            sendSuccess(textChannel, author, " usuário " + user.getAsMention() + " punido com sucesso.");
            return;
        } catch (Exception e) {
            if (e.getMessage() == null || !e.getMessage().equals("Can't modify a member with higher or equal highest role than yourself!")) {
                e.printStackTrace();
                SimpleLogger.sendStackTraceToOwner(e);
            }
        }
        sendError(textChannel, author, "não foi possível punir esse usuário, **talvez** eu não tenha permissão.", 30);
    }

    public Date getDefaultEndDate(Date now) {
        return null;
    }

}
