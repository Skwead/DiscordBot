package br.com.finalelite.discord.bot.entity.command;

import br.com.finalelite.discord.bot.Bot;
import br.com.finalelite.discord.bot.entity.punishment.Punishment;
import br.com.finalelite.discord.bot.entity.punishment.PunishmentType;
import br.com.finalelite.discord.bot.utils.DiscordUtils;
import br.com.finalelite.discord.bot.utils.SimpleLogger;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.var;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

public abstract class PunishmentCommandBase extends CommandBase {

    private PunishmentType type;

    public PunishmentCommandBase(String name, String description, CommandPermission permission, PunishmentType type) {
        super(
                name,
                description,
                permission,
                CommandChannelChecker.DISABLE,
                DefaultCommandCategory.MODERATION
        );
        this.type = type;
    }

    protected abstract String getErrorMessage();

    @Override

    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        if (!isArgumentsValid(message, args)) {
            sendError(textChannel, author, getErrorMessage(), 30);
            return;
        }

        val user = message.getMentionedUsers().get(0);
        var reason = "Nenhum motivo mencionado";
        if (args.length >= 2)
            reason = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));

        var proof = "Nenhuma prova mencionada";
        if (message.getAttachments().size() != 0) {
            val link = DiscordUtils.uploadToImgur(message);
            if (link == null)
                return;
            proof = link;
        }

        val now = new Date();

        val endResult = getEndDate(now, message, args);

        if (!endResult.valid) {
            sendError(textChannel, author, getErrorMessage(), 30);
            return;
        }

        val end = endResult.date;

        try {
            val punishment = Punishment.builder()
                    .authorId(author.getId())
                    .relatedGuildId(guild.getId())
                    .type(type)
                    .dateSeconds(Punishment.parseDate(now))
                    .endSeconds(Punishment.parseDate(end))
                    .reason(reason)
                    .proof(proof)
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

    protected abstract boolean isArgumentsValid(Message message, String[] args);

    public abstract EndDateResult getEndDate(Date now, Message message, String[] args);

    @RequiredArgsConstructor
    protected static class EndDateResult {
        private final boolean valid;
        private final Date date;
    }

}
