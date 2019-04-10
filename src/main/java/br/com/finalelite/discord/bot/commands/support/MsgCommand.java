package br.com.finalelite.discord.bot.commands.support;

import br.com.finalelite.discord.bot.Bot;
import br.com.finalelite.discord.bot.entity.command.Command;
import br.com.finalelite.discord.bot.entity.command.CommandChannelChecker;
import br.com.finalelite.discord.bot.entity.command.CommandPermission;
import br.com.finalelite.discord.bot.entity.command.DefaultCommandCategory;
import br.com.finalelite.discord.bot.entity.ticket.Ticket;
import lombok.val;
import lombok.var;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class MsgCommand extends Command {
    private final static Map<String, String> messages = Bot.getInstance().getConfig().getMessages();
    private static Map<String, PlaceHolder> placeHolders = new HashMap<>();

    static {
        addPlaceHolder("user_mention", (ticket, author, message, channel, guild) -> {
            if (ticket == null)
                return "";
            else
                return Bot.getInstance().getJda().getUserById(ticket.getUserId()).getAsMention();
        });
        addPlaceHolder("list", (ticket, author, message, channel, guild) -> "`" + String.join(", ", messages.keySet()) + "`");
    }

    public MsgCommand() {
        super(
                "msg",
                "envia uma mensagem pre-definida",
                CommandPermission.SUPPORT,
                CommandChannelChecker.TICKET_MANAGEMENT_AND_STAFF,
                DefaultCommandCategory.SUPPORT
        );
    }

    private static void addPlaceHolder(String key, PlaceHolder placeHolder) {
        placeHolders.put(key.toLowerCase(), placeHolder);
    }

    private static String format(String text, Message message, Guild guild, TextChannel channel, User author) {
        val ticket = channel.getId().equals(Bot.getInstance().getConfig().getStaffChannelId()) ? null : Bot.getInstance().getDatabase().getTicketByChannelId(channel.getId());
        val pattern = Pattern.compile("\\$\\{\\w*}");
        val matcher = pattern.matcher(text);
        var newText = text;

        while (matcher.find()) {
            val group = matcher.group();
            val key = group.substring(2, group.length() - 1);

            if (placeHolders.containsKey(key.toLowerCase())) {
                val newString = placeHolders.get(key.toLowerCase()).get(ticket, message, guild, channel, author);
                newText = newText.replace(group, newString);
            }
        }
        return newText;
    }

    @Override
    public void run(Message message, Guild guild, TextChannel channel, User author, String[] args) {
        if (args.length == 0) {
            message.delete().complete();
            return;
        }
        val arg = args[0];
        val msg = messages.get(arg.toLowerCase());
        if (msg != null)
            channel.sendMessage(Objects.requireNonNull(format(msg, message, guild, channel, author))).complete();

        message.delete().complete();
    }

    @FunctionalInterface
    public interface PlaceHolder {
        String get(Ticket ticket, Message message, Guild guild, TextChannel channel, User author);
    }

}
