package br.com.finalelite.bots.supporter.command.commands.moderation.utils;

import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.command.CommandChannelChecker;
import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.DefaultCommandCategory;
import br.com.finalelite.bots.supporter.utils.SimpleLogger;
import br.com.finalelite.bots.supporter.utils.time.LongUnits;
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

    public TempPunishmentCommand(PunishmentType type, String name, String description, CommandPermission permission) {
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
        var time = 0;
        try {
            time = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sendError(textChannel, author, "use `!" + getName() + " <usuário> <tempo> <unidade de tempo> [<motivo>]` (1).", 30);
            return;
        }

        val timeUnit = LongUnits.getUnitByName(args[2]);
        if (timeUnit == null) {
            sendError(textChannel, author, "use `!" + getName() + " <usuário> <tempo> <unidade de tempo> [<motivo>]` (2).", 30);
            return;
        }

        var reason = "Nenhum motivo mencionado";
        if (args.length >= 4)
            reason = Arrays.stream(args).skip(3).collect(Collectors.joining(" "));

        val now = new Date();
        try {
            val punishment = Punishment.builder()
                    .author(guild.getMember(author))
                    .relatedGuild(guild)
                    .type(type)
                    .date(now)
                    .end(new Date((long) (now.getTime() + timeUnit.convert(time, LongUnits.MILLISECONDS))))
                    .reason(reason)
                    .target(guild.getMember(user)
                    );

            ModerationUtils.apply(punishment.build());
            sendSuccess(textChannel, author, " usuário " + user.getAsMention() + " punido com sucesso.");
            return;
        } catch (Exception e) {
            if (e.getMessage() == null || !e.getMessage().equals("Can't modify a member with higher or equal highest role than yourself!")) {
                e.printStackTrace();
                SimpleLogger.sendStackTraceToOwner(e);
            }
        }
        sendError(textChannel, author, "não foi possível punir esse usuário, talvez eu não tenha permissão.", 30);
    }


}
