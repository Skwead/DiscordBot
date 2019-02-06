package br.com.finalelite.bots.supporter.command.commands.moderation.utils;

import br.com.finalelite.bots.supporter.Supporter;
import br.com.finalelite.bots.supporter.utils.SimpleLogger;
import lombok.val;
import net.dv8tion.jda.core.EmbedBuilder;

public class ModerationUtils {

    private static final String banEmoji = "<:blobban:531459039998115840>";

    public static void logModeration(Punishment punishment) {
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

    public static void apply(Punishment punishment) {
        punishment.apply();
        logModeration(punishment);
        Supporter.getInstance().getDatabase().addPunishment(punishment);
    }

    public static String formatDuration(int durationInSeconds) {
        val years = durationInSeconds / 31557600;
        val months = (durationInSeconds % 31557600) / 2629800;
        val weeks = (durationInSeconds % 2629800) / 604800;
        val days = (durationInSeconds % 604800) / 86400;
        val hours = (durationInSeconds % 86400) / 3600;
        val minutes = (durationInSeconds % 3600) / 60;
        val seconds = durationInSeconds % 60;

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
