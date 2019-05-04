package com.github.pauloo27.discord.bot.commands.support;

import com.github.pauloo27.discord.bot.Bot;
import com.github.pauloo27.discord.bot.entity.command.CommandBase;
import com.github.pauloo27.discord.bot.entity.command.CommandChannelChecker;
import com.github.pauloo27.discord.bot.entity.command.CommandPermission;
import com.github.pauloo27.discord.bot.entity.command.DefaultCommandCategory;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class RenameCommand extends CommandBase {


    public RenameCommand() {
        super(
                "renomear",
                "renomeia o ticket",
                CommandPermission.SUPPORT,
                CommandChannelChecker.TICKET_MANAGEMENT,
                DefaultCommandCategory.SUPPORT
        );
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        val text = String.join(" ", args);
        if (text.isEmpty()) {
            sendError(textChannel, author, "use `!renomear <nome>`.", 10);
            return;
        }

        if (text.length() > 32) {
            sendError(textChannel, author, "o nome deve ter no máximo 32 de tamanho.", 10);
            return;
        }

        val database = Bot.getInstance().getDatabase();
        val ticket = database.getTicketByChannelId(textChannel.getId());

        textChannel.getManager().setName(ticket.getStatus().getEmoji() + "-ticket-" + text.replace(" ", "-")).complete();

        ticket.setName(text.replace(" ", "-"));
        database.updateTicket(ticket);

        message.delete().complete();
    }
}
