package com.github.pauloo27.discord.bot.manager;

import com.github.pauloo27.discord.bot.Bot;
import com.github.pauloo27.discord.bot.entity.punishment.Punishment;
import com.github.pauloo27.discord.bot.utils.DiscordUtils;
import com.github.pauloo27.discord.bot.utils.SimpleLogger;
import com.github.pauloo27.discord.bot.utils.time.TimeUnits;
import lombok.val;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicLong;

public class PunishmentManager {

    private static final String banEmoji = "<:blobban:531459039998115840>";
    private static final String revertEmoji = ":leftwards_arrow_with_hook: ";

    public static long getUnitAndUpdateDuration(long unitInSeconds, AtomicLong durationInSeconds) {
        val unitValue = durationInSeconds.get() / unitInSeconds;
        durationInSeconds.set(durationInSeconds.get() - unitInSeconds * unitValue);
        return unitValue;
    }

    public void logApplyModeration(Punishment punishment) {
        val author = punishment.getAuthor();
        val target = punishment.getTarget();

        val embed = new EmbedBuilder()
                .setColor(0xf1c65f)
                .setAuthor("Final Elite", "https://finalelite.com.br", Bot.getInstance().getJda().getSelfUser().getAvatarUrl())
                .setDescription(String.format("%s **O usuário %s (%s) foi punido!**", banEmoji, target.getAsMention(), target.getUser().getId()))

                .addField(":hammer_pick: Autor", String.format("%s", author.getAsMention()), true)
                .addField(":gun: Puniçao", punishment.getType().getDisplayName(), true)
                .addField(":pen_ballpoint: Motivo", punishment.getReason(), true)

                .setFooter(String.format("%s#%s - %s", author.getEffectiveName(), author.getUser().getDiscriminator(), SimpleLogger.format(punishment.getDate())),
                        author.getUser().getAvatarUrl());
        if (!punishment.getType().isPermanent()) {
            embed.addField(":timer: Duração",
                    TimeUnits.formatDuration((int) ((punishment.getEnd().getTime() - punishment.getDate().getTime()) / 1000)),
                    true);
            embed.addField(":clock: Fim", SimpleLogger.format(punishment.getEnd()), true);
        }

        if (punishment.isNsfw()) {
            embed.addField(":beginner: Prova", "(NSFW)", true);
        } else {
            embed.addField(":beginner: Prova", punishment.getProof() == null ? "Nenhuma prova mencionada" : punishment.getProof(), true);
            if (punishment.getProof() != null && punishment.getProof().matches(DiscordUtils.IMAGE_URL_REGEX)) {
                embed.setImage(punishment.getProof());
            }
        }

        val supporter = Bot.getInstance();
        val channel = supporter.getJda().getTextChannelById(supporter.getConfig().getModLogId());
        channel.sendMessage(embed.build()).queue();
        //sendProof(channel, punishment);
    }

    public void sendProof(TextChannel channel, Punishment punishment) {
        try {
            val userAvatar = DiscordUtils.getUserAvatar(punishment.getAuthor().getUser());
            val base = ImageIO.read(Bot.class.getResourceAsStream("/message_base.png"));

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

    public void logRevertModeration(Punishment punishment) {
        val author = punishment.getAuthor();
        val target = punishment.getTarget();

        val authorId = author == null ? punishment.getAuthorId() : null;
        val targetId = target == null ? punishment.getTargetId() : null;

        val embed = new EmbedBuilder()
                .setColor(0xf27c5e)
                .setAuthor("Final Elite", "https://finalelite.com.br", Bot.getInstance().getJda().getSelfUser().getAvatarUrl())
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

        val supporter = Bot.getInstance();
        val channel = supporter.getJda().getTextChannelById(supporter.getConfig().getModLogId());
        channel.sendMessage(embed.build()).queue();
    }

    public void apply(Punishment punishment) {
        punishment.apply();
        logApplyModeration(punishment);
        Bot.getInstance().getDatabase().addPunishment(punishment);
    }

    public void revert(Punishment punishment) {
        punishment.revert();
        logRevertModeration(punishment);
        Bot.getInstance().getDatabase().revertPunishment(punishment);
    }

}
