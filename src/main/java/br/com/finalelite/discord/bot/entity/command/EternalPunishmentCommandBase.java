package br.com.finalelite.discord.bot.entity.command;

import br.com.finalelite.discord.bot.entity.punishment.PunishmentType;
import lombok.val;
import net.dv8tion.jda.core.entities.Message;

import java.util.Date;

public class EternalPunishmentCommandBase extends PunishmentCommandBase {
    public EternalPunishmentCommandBase(String name, String description, CommandPermission permission, PunishmentType type) {
        super(name, description, permission, type);
    }

    @Override
    protected String getErrorMessage() {
        return "use `" + getDisplayName() + " <usuário> [<motivo>]`. A prova em imagem pode ser anexada.";
    }

    @Override
    protected boolean isArgumentsValid(Message message, String[] args) {
        val guild = message.getGuild();

        return !(message.getMentionedUsers().size() < 1 || args.length < 1 || !args[0].equals(guild.getMember(message.getMentionedUsers().get(0)).getAsMention()));
    }

    @Override
    public EndDateResult getEndDate(Date now, Message message, String[] args) {
        return new EndDateResult(true, null);
    }
}
