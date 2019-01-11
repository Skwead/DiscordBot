package br.com.finalelite.bots.supporter.command.commands.moderation;

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
                .setDescription(String.format("%s **O usuário %s#%s (%s) foi punido!**", banEmoji, target.getEffectiveName(), target.getUser().getDiscriminator(), target.getUser().getId()))

                .addField(":hammer_pick: Autor", String.format("%s#%s", author.getEffectiveName(), author.getUser().getDiscriminator()), true)
                .addField(":gun: Puniçao", punishment.getType().name(), true)
                .addField(":pen_ballpoint: Motivo", punishment.getReason(), true)

                .setFooter(String.format("%s#%s", author.getEffectiveName(), author.getUser().getDiscriminator()),
                        author.getUser().getAvatarUrl());
        if (!punishment.getType().isPermanent())
            embed.addField(":clock: Fim", SimpleLogger.format(punishment.getDate()), true);

        val supporter = Supporter.getInstance();
        val channel = supporter.getJda().getTextChannelById(supporter.getConfig().getModLogId());
        channel.sendMessage(embed.build()).complete();
    }

    public static void apply(Punishment punishment) {
        punishment.apply();
        logModeration(punishment);
    }

}
