package br.com.finalelite.discord.bot.entity.command;

import br.com.finalelite.discord.bot.Bot;
import br.com.finalelite.discord.bot.entity.punishment.Punishment;
import br.com.finalelite.discord.bot.entity.punishment.PunishmentType;
import br.com.finalelite.discord.bot.utils.SimpleLogger;
import br.com.finalelite.discord.bot.utils.time.TimeUnits;
import lombok.val;
import lombok.var;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

public abstract class TempPunishmentCommand extends Command {

    private PunishmentType type;

    public TempPunishmentCommand(String name, String description, CommandPermission permission, PunishmentType type) {
        super(name, description, permission, CommandChannelChecker.DISABLE, DefaultCommandCategory.MODERATION);
        this.type = type;
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        if (message.getMentionedUsers().size() < 1 || args.length < 3 || !args[0].equals(message.getMentionedUsers().get(0).getAsMention())) {
            sendError(textChannel, author, "use `!" + getName() + " <usuário> <tempo> <unidade de tempo> [<motivo>]` (0).", 30);
            return;
        }

        val user = message.getMentionedUsers().get(0);

        val now = new Date();
        val end = (Date) now.clone();
        var endsWithComma = false;
        var argumentIndex = 1;
        do {
            var time = 0;
            try {
                time = Integer.parseInt(args[argumentIndex]);
            } catch (NumberFormatException e) {
                sendError(textChannel, author, "use `!" + getName() + " <usuário> <tempo> <unidade de tempo> [<motivo>]` (1).", 30);
                return;
            }

            val rawArgument = args[argumentIndex + 1];

            endsWithComma = rawArgument.endsWith(",");

            val rawTimeUnit = endsWithComma ? rawArgument.substring(0, rawArgument.length() - 1) : rawArgument;

            val timeUnit = TimeUnits.getUnitByName(rawTimeUnit);
            if (timeUnit == null) {
                sendError(textChannel, author, "use `!" + getName() + " <usuário> <tempo> <unidade de tempo> [<motivo>]` (2).", 30);
                return;
            }

            end.setTime((long) (end.getTime() + timeUnit.convert(time, TimeUnits.MILLISECONDS)));
            argumentIndex += 2;
        } while (endsWithComma);

        var reason = Arrays.stream(args).skip(argumentIndex).collect(Collectors.joining(" "));
        if (reason.isEmpty())
            reason = "Nenhum motivo mencionado";

        if (reason.length() > 256) {
            sendError(textChannel, author, "motivo muito longo.", 30);
            return;
        }

        try {
            val punishment = Punishment.builder()
                    .authorId(author.getId())
                    .relatedGuildId(guild.getId())
                    .type(type)
                    .dateSeconds(Punishment.parseDate(now))
                    .endSeconds(Punishment.parseDate(end))
                    .reason(reason)
                    .targetId(user.getId());

            val built = punishment.build();

            Bot.getInstance().getPunishmentManager().apply(built);
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


}
