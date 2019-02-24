package br.com.finalelite.bots.supporter.command.commands.moderation.utils;

import br.com.finalelite.bots.supporter.Supporter;
import br.com.finalelite.bots.supporter.utils.DiscordUtils;
import br.com.finalelite.bots.supporter.utils.SimpleLogger;
import lombok.val;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.TextChannel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicLong;

public class ModerationUtils {

    private static final String banEmoji = "<:blobban:531459039998115840>";
    private static final String revertEmoji = ":leftwards_arrow_with_hook: ";

    public static void logApplyModeration(Punishment punishment) {
        val author = punishment.getAuthor();
        val target = punishment.getTarget();

        val embed = new EmbedBuilder()
                .setColor(0xf1c65f)
                .setAuthor("Final Elite", "https://finalelite.com.br", Supporter.getInstance().getJda().getSelfUser().getAvatarUrl())
                .setDescription(String.format("%s **O usuário %s (%s) foi punido!**", banEmoji, target.getAsMention(), target.getUser().getId()))

                .addField(":hammer_pick: Autor", String.format("%s", author.getAsMention()), true)
                .addField(":gun: Puniçao", punishment.getType().getDisplayName(), true)
                .addField(":pen_ballpoint: Motivo", punishment.getReason(), true)

                .setFooter(String.format("%s#%s - %s", author.getEffectiveName(), author.getUser().getDiscriminator(), SimpleLogger.format(punishment.getDate())),
                        author.getUser().getAvatarUrl());
        if (!punishment.getType().isPermanent()) {
            embed.addField(":timer: Duração",
                    formatDuration((int) ((punishment.getEnd().getTime() - punishment.getDate().getTime()) / 1000)),
                    true);
            embed.addField(":clock: Fim", SimpleLogger.format(punishment.getEnd()), true);
        }

        val supporter = Supporter.getInstance();
        val channel = supporter.getJda().getTextChannelById(supporter.getConfig().getModLogId());
        channel.sendMessage(embed.build()).queue();
        //sendProof(channel, punishment);
    }


    public static void sendProof(TextChannel channel, Punishment punishment) {
        try {
            val userAvatar = DiscordUtils.getUserAvatar(punishment.getAuthor().getUser());
            val base = ImageIO.read(Supporter.class.getResourceAsStream("/message_base.png"));

            val image = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
            val graphic = image.getGraphics();
            val username = punishment.getAuthor().getEffectiveName();

            // avatar
            graphic.drawImage(userAvatar, 15, 15, 40, 40, null);
            // background
            graphic.drawImage(base, 0, 0, null);

            // username
            graphic.setColor(Color.WHITE);
            graphic.setFont(new Font("Arial", Font.PLAIN, 14));
            val nameWidth = graphic.getFontMetrics().stringWidth(username);
            val nameHeight = graphic.getFontMetrics().getHeight();
            graphic.drawString(username, 15 + 40 + 20, 15 + nameHeight);

            // date
            graphic.setColor(new Color(0x57595e));
            graphic.setFont(new Font("Arial", Font.PLAIN, 12));
            val formatter = new SimpleDateFormat("hh:mm a");
            val formattedDate = "Hoje às " + formatter.format(punishment.getDate());
            val dateHeight = graphic.getFontMetrics().getHeight();
            graphic.drawString(formattedDate, 15 + 40 + 20 + 5 + nameWidth, 3 + 15 + dateHeight);

            // message
            graphic.setColor(new Color(0xbbbdbf));
            graphic.setFont(new Font("Arial", Font.PLAIN, 13));
            graphic.drawString(punishment.getReason(), 15 + 40 + 20, 37 + 15);

            val bytes = new ByteArrayOutputStream();
            ImageIO.write(image, "png", bytes);
            channel.sendFile(bytes.toByteArray(), "proof.png").queue();
        } catch (IOException e) {
            SimpleLogger.sendStackTraceToOwner(e);
            e.printStackTrace();
        }
    }

    @SuppressWarnings("Duplicates")
    public static void sendReasonAsMessage(TextChannel channel, Punishment punishment) {
        try {
            val format = new SimpleDateFormat("hh:mm a");
            val connection = (HttpURLConnection) new URL(punishment.getAuthor().getUser().getAvatarUrl()).openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/4.76");
            val userImage = ImageIO.read(connection.getInputStream());
            val image = new BufferedImage(500, 200, BufferedImage.TYPE_INT_ARGB);
            val graphic = image.getGraphics();
            graphic.setColor(new Color(0x36393f));
            graphic.fillRect(0, 0, 500, 200);
            graphic.drawImage(userImage, 20, 20, 50, 50, null);
            graphic.setColor(new Color(0xFFFFFF));
            graphic.setFont(new Font("Arial", Font.PLAIN, 20));
            graphic.drawString(punishment.getReason(), 20 + 20 + 10, 20 + 50 + 20);
            graphic.drawString(punishment.getAuthor().getNickname() == null ?
                    punishment.getAuthor().getEffectiveName() :
                    punishment.getAuthor().getNickname(), 20 + 50 + 20, 40);
            graphic.setColor(new Color(0x2C2F33));
            graphic.setFont(new Font("Arial", Font.PLAIN, 14));
            graphic.drawString("Hoje às " + format.format(punishment.getDate()), 20 + 50 + 20 + 100, 40);
            val bytes = new ByteArrayOutputStream();
            ImageIO.write(image, "png", bytes);
            channel.sendFile(bytes.toByteArray(), "proof.png", new MessageBuilder("eita").build()).complete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logRevertModeration(Punishment punishment) {
        val author = punishment.getAuthor();
        val target = punishment.getTarget();

        val authorId = author == null ? punishment.getAuthorId() : null;
        val targetId = target == null ? punishment.getTargetId() : null;

        val embed = new EmbedBuilder()
                .setColor(0xf27c5e)
                .setAuthor("Final Elite", "https://finalelite.com.br", Supporter.getInstance().getJda().getSelfUser().getAvatarUrl())
                .setDescription(String.format("%s **O usuário %s (%s) foi despunido!**", revertEmoji,
                        targetId == null ? target.getAsMention() : "<@" + targetId + ">", targetId == null ? target.getUser().getId() : targetId))

                .addField(":hammer_pick: Autor", String.format("%s", authorId == null ? author.getAsMention() : "<@" + authorId + ">"), true)
                .addField(":gun: Puniçao", punishment.getType().getDisplayName(), true)
                .addField(":pen_ballpoint: Motivo", punishment.getReason(), true)

                .setFooter(authorId == null ?
                                String.format("%s#%s - %s", author.getEffectiveName(), author.getUser().getDiscriminator(), SimpleLogger.format(punishment.getDate()))
                                :
                                String.format("%s - %s", authorId, SimpleLogger.format(punishment.getDate())),
                        authorId == null ?
                                author.getUser().getAvatarUrl() : null);

        val supporter = Supporter.getInstance();
        val channel = supporter.getJda().getTextChannelById(supporter.getConfig().getModLogId());
        channel.sendMessage(embed.build()).queue();
    }

    public static void apply(Punishment punishment) {
        punishment.apply();
        logApplyModeration(punishment);
        Supporter.getInstance().getDatabase().addPunishment(punishment);
    }

    public static void revert(Punishment punishment) {
        punishment.revert();
        logRevertModeration(punishment);
        Supporter.getInstance().getDatabase().revertPunishment(punishment);
    }

    public static long getUnitAndUpdateDuration(long unitInSeconds, AtomicLong durationInSeconds) {
        val unitValue = durationInSeconds.get() / unitInSeconds;
        durationInSeconds.set(durationInSeconds.get() - unitInSeconds * unitValue);
        return unitValue;
    }

    public static String formatDuration(long durationInSeconds) {
        val reamingSeconds = new AtomicLong(durationInSeconds);

        val years = getUnitAndUpdateDuration(31536000, reamingSeconds);
        val months = getUnitAndUpdateDuration(2592000, reamingSeconds);
        val weeks = getUnitAndUpdateDuration(604800, reamingSeconds);
        val days = getUnitAndUpdateDuration(86400, reamingSeconds);
        val hours = getUnitAndUpdateDuration(3600, reamingSeconds);
        val minutes = getUnitAndUpdateDuration(60, reamingSeconds);
        val seconds = getUnitAndUpdateDuration(1, reamingSeconds);

        StringBuilder sb = new StringBuilder();

        if (years >= 1) {
            sb.append(years);
            sb.append(years == 1 ? " ano" : " anos");
        }


        if (months >= 1) {
            if (sb.length() != 0)
                sb.append(weeks == 0 ? " e " : ", ");

            sb.append(months);
            sb.append(months == 1 ? " mês" : " meses");
        }

        if (weeks >= 1) {
            if (sb.length() != 0)
                sb.append(days == 0 ? " e " : ", ");

            sb.append(weeks);
            sb.append(weeks == 1 ? " semana" : " semanas");
        }

        if (days >= 1) {
            if (sb.length() != 0)
                sb.append(minutes == 0 ? " e " : ", ");

            sb.append(days);
            sb.append(days == 1 ? " dia" : " dias");
        }

        if (hours >= 1) {
            if (sb.length() != 0)
                sb.append(minutes == 0 ? " e " : ", ");

            sb.append(hours);
            sb.append(hours == 1 ? " hora" : " horas");
        }

        if (minutes >= 1) {
            if (sb.length() != 0)
                sb.append(seconds == 0 ? " e " : ", ");

            sb.append(minutes);
            sb.append(minutes == 1 ? " minuto" : " minutos");
        }

        if (seconds >= 1) {
            if (sb.length() != 0)
                sb.append(" e ");

            sb.append(seconds);
            sb.append(seconds == 1 ? " segundo" : " segundos");
        }
        return sb.toString();
    }
}
