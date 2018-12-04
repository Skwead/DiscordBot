package br.com.finalelite.bots.supporter.command.commands;

import br.com.finalelite.bots.supporter.Main;
import br.com.finalelite.bots.supporter.command.Command;
import lombok.val;
import lombok.var;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class MsgCommand extends Command {
    public MsgCommand() {
        super("msg", true, false, true, false);
    }

    private static Map<String, String> messages = new HashMap<>();
    private static Map<String, PlaceHolder> placeHolders = new HashMap<>();

    private static void addMessage(String key, String message) {
        messages.put(key.toLowerCase(), message);
    }

    private static void addPlaceHolder(String key, PlaceHolder placeHolder) {
        placeHolders.put(key.toLowerCase(), placeHolder);
    }

    static {
        addMessage("bemvindo", "Olá ${user-mention}, seja bem-vindo ao nosso suporte. Como posso ajudar?");
        addMessage("obrigado", "Obrigado por entrar em contanto com nosso suporte. Estaremos aqui caso precise de ajuda.");
        addMessage("resolvido", "Seu problema foi resolvido? Se sim, você já pode fechar esse chat. Use `!fechar`.");
        addMessage("tagvip", "Para que possamos adicionar sua tag VIP no Discord, precisamos que você informe o seu Nick, o VIP adquirido e o ID da compra.");
        addMessage("ativarvip", "Para que ativar seu VIP no Minecraft, precisamos do email usado no cadastro do site e seu Nick no jogo.");
        addMessage("bug", "Obrigado por reportar esse problema. Nós passaremos para nossa equipe de desenvolvimento.");
        addMessage("jogador", "Obrigado por reportar esse jogador. Nós analisaremos a sua denuncia e tomaremos as devidas atitudes.");

        addPlaceHolder("user-mention", (ticket, author, message, channel, guild) -> Main.getJda().getUserById(ticket.getUserId()).getAsMention());
    }

    @Override
    public void run(Message message, Guild guild, TextChannel channel, User author, String[] args) {
        val arg = args[0];
        val msg = messages.get(arg.toLowerCase());
        if (msg != null)
            channel.sendMessage(Objects.requireNonNull(format(msg, message, guild, channel, author))).complete();

        message.delete().complete();
    }

    private static String format(String text, Message message, Guild guild, TextChannel channel, User author) {
        try {
            val ticket = Main.getDb().getTicketByChannelId(channel.getId());
            val pattern = Pattern.compile("\\$\\{.*}");
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
