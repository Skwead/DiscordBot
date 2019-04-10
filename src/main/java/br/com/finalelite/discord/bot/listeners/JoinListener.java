package br.com.finalelite.discord.bot.listeners;

import br.com.finalelite.discord.bot.Bot;
import br.com.finalelite.discord.bot.command.commands.moderation.utils.PunishmentType;
import br.com.finalelite.discord.bot.utils.DiscordUtils;
import br.com.finalelite.discord.bot.utils.SimpleLogger;
import lombok.val;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class JoinListener extends ListenerAdapter {

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        val supporter = Bot.getInstance();
        // lets block someone of "steal" the bot
        if (supporter.getJda().getGuilds().size() == 0)
            return; // oh, okay this is the first guild
        // grr, seems like someone else get the bot invite
        supporter.shutdown(String.format("The bot is in %d guilds. For security, the bot only run in the official guild.", supporter.getJda().getGuilds().size()));
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        val supporter = Bot.getInstance();
        // send a welcome message to the new user :)
        val user = event.getUser();
        val pv = event.getUser().openPrivateChannel().complete();
        if (pv == null) // i don't know if this is possible, but lets check
            return;

        val ban = supporter.getDatabase().getActivePunishmentByUser(event.getUser().getId(), PunishmentType.TEMP_BAN);
        if (ban != null) {
            SimpleLogger.log("%s#%s (%s) did an ooopsie: %s%n", user.getName(), user.getDiscriminator(), user.getId(), ban.getReason());
            pv.sendMessage(new MessageBuilder()
                    .setContent(
                            String.format("**Vocẽ não pode entrar no nosso Discord por estar banido.**" +
                                            "\nPunidor por: %s" +
                                            "\nMotivo: %s" +
                                            "\nAcaba em: %s" +
                                            "\n\nSe a punição foi injusta, entre em contato no email `contato@finalelite.com.br`.",
                                    ban.getAuthor().getNickname() == null ? ban.getAuthor().getEffectiveName() : ban.getAuthor().getNickname(),
                                    ban.getReason(),
                                    SimpleLogger.format(ban.getEnd())))
                    .build()
            ).complete();
            event.getGuild().getController().kick(event.getGuild().getMember(user), ban.getReason()).complete();
            return;
        }

        if (Bot.getInstance().getConfig().getWelcomeMessage() == null) // oh, there's no welcome message :(
            return;

        try {
            // get the user picture and put in the server's image
            val userImage = DiscordUtils.getUserAvatar(user); // get the user avatar
            val baseImage = ImageIO.read(Bot.class.getResourceAsStream("/image.png")); // get the base image
            val image = new BufferedImage(906, 398, BufferedImage.TYPE_INT_ARGB); // create some place to build the image
            val graphic = image.getGraphics();
            graphic.drawImage(userImage, 367, 26, 178, 178, null); // draw the user avatar
            graphic.drawImage(baseImage, 0, 0, null); // draw the base image
            val bytes = new ByteArrayOutputStream();
            ImageIO.write(image, "png", bytes); // get the bytes
            pv.sendFile(bytes.toByteArray(), "welcome.png", new MessageBuilder(supporter.getConfig().getWelcomeMessage()).build()).complete(); // send
        } catch (IOException e) {
            // >:( we cannot send the image, so lets just send the message
            pv.sendMessage(new MessageBuilder(Bot.getInstance().getConfig().getWelcomeMessage()).build()).complete();
        }
    }

}
