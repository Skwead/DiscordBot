package br.com.finalelite.bots.supporter.command.commands.moderation.utils;

import br.com.finalelite.bots.supporter.Supporter;
import br.com.finalelite.bots.supporter.utils.SimpleLogger;
import lombok.val;
import net.dv8tion.jda.core.EmbedBuilder;

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
        channel.sendMessage(embed.build()).complete();
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
        channel.sendMessage(embed.build()).complete();
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
        val remaingSeconds = new AtomicLong(durationInSeconds);

        val years = getUnitAndUpdateDuration(31536000, remaingSeconds);
        val months = getUnitAndUpdateDuration(2592000, remaingSeconds);
        val weeks = getUnitAndUpdateDuration(604800, remaingSeconds);
        val days = getUnitAndUpdateDuration(86400, remaingSeconds);
        val hours = getUnitAndUpdateDuration(3600, remaingSeconds);
        val minutes = getUnitAndUpdateDuration(60, remaingSeconds);
        val seconds = getUnitAndUpdateDuration(1, remaingSeconds);

//        val years = durationInSeconds / 31536000;
//        val months = (durationInSeconds % 31536000) / 2592000;
//        val weeks = (durationInSeconds % 2592000) / 604800;
//        val days = (durationInSeconds % 604800) / 86400;
//        val hours = (durationInSeconds % 86400) / 3600;
//        val minutes = (durationInSeconds % 3600) / 60;
//        val seconds = durationInSeconds % 60;

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
