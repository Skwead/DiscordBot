package com.github.pauloo27.discord.bot.commands.moderation;

import com.github.pauloo27.discord.bot.Bot;
import com.github.pauloo27.discord.bot.entity.command.CommandBase;
import com.github.pauloo27.discord.bot.entity.command.CommandChannelChecker;
import com.github.pauloo27.discord.bot.entity.command.CommandPermission;
import com.github.pauloo27.discord.bot.entity.command.DefaultCommandCategory;
import com.github.pauloo27.discord.bot.entity.punishment.Punishment;
import com.github.pauloo27.discord.bot.utils.DiscordUtils;
import lombok.Data;
import lombok.val;
import lombok.var;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PunishCommand extends CommandBase {

    /* The message id > the user id */
    private Map<String, PunishmentRequest> punishmentEmbeds = new HashMap<>();

    public PunishCommand() {
        super(
                "punir",
                "pune um usuário no Discord",
                CommandPermission.SUPPORT,
                CommandChannelChecker.DISABLE,
                DefaultCommandCategory.MODERATION
        );

        Bot.getInstance().getJda().addEventListener(new ReactionListener());
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        if (message.getMentionedUsers().size() < 1 || args.length < 1 || !args[0].equals(guild.getMember(message.getMentionedUsers().get(0)).getAsMention())) {
            sendError(textChannel, author, "use `" + getDisplayName() + " <usuário> [<prova>]`.", 30);
            return;
        }

        val user = message.getMentionedUsers().get(0);
        val proofURL = args.length >= 2 ? Arrays.stream(args).skip(1).collect(Collectors.joining(" ")) : null;

        if (proofURL != null && proofURL.length() > 256) {
            sendError(textChannel, author, "prova muito longa, por favor, use uma prova menor ou igual a 256.", 30);
            return;
        }
        @SuppressWarnings("RedundantCast")
        var link = (String) null;
        if (message.getAttachments().size() != 0 && proofURL == null) {
            link = DiscordUtils.uploadToImgur(message);
            if (link == null)
                return;
        }

        val proof = link == null ? proofURL : link;

        val sb = new StringBuilder();

        val rules = Bot.getInstance().getConfigManager().getConfig().getRules();

        sb.append("Escolha um motivo dos citados abaixo ou :x: para cancelar:\n");

        rules.forEach((name, rule) ->
                sb.append(rule.getEmoji())
                        .append(" - **")
                        .append(name)
                        .append("**")
                        .append("\n"));

        val embed = new EmbedBuilder()
                .setAuthor("Final Elite", "https://finalelite.com.br", Bot.getInstance().getJda().getSelfUser().getAvatarUrl())

                .appendDescription(sb.toString())

                .setFooter(
                        String.format("%s#%s - %s#%s",
                                author.getName(),
                                author.getDiscriminator(),
                                user.getName(),
                                user.getDiscriminator()),
                        author.getAvatarUrl())

                .setColor(0x70e5b0);

        textChannel.sendMessage(embed.build())
                .queue(sentMessage -> {
                    punishmentEmbeds.put(sentMessage.getId(), new PunishmentRequest(author.getId(), user.getId(), proof));
                    sentMessage.addReaction("\u274C").queue();
                    rules.forEach((name, rule) ->
                            sentMessage.addReaction(rule.getEmoji()).queue());
                });

        message.delete().complete();
    }

    private class ReactionListener extends ListenerAdapter {
        @Override
        public void onMessageReactionAdd(MessageReactionAddEvent event) {
            val user = event.getUser();
            val messageId = event.getMessageId();

            if (user.isBot()) return;

            if (!punishmentEmbeds.containsKey(messageId)) return;

            if (!punishmentEmbeds.get(messageId).getUserId().equalsIgnoreCase(user.getId())) return;

            val embed = punishmentEmbeds.get(messageId);

            val rules = Bot.getInstance().getConfigManager().getConfig().getRules();

            if (event.getReactionEmote().getName().equals("\u274C")) {
                Bot.getMessageById(event.getTextChannel().getId(), messageId).delete().complete();
                punishmentEmbeds.remove(messageId);
                return;
            }

            val reason = rules.entrySet().stream()
                    .filter(entry -> entry.getValue().getEmoji()
                            .equalsIgnoreCase(event.getReactionEmote().getName()))
                    .findFirst().orElse(null);

            if (reason == null) {
                sendError(event.getTextChannel(), user, "punição inválida.", 30);
                return;
            }

            val name = reason.getKey();
            val rule = reason.getValue();
            val now = new Date();

            val proof = embed.getProof();

            val punishment = Punishment.builder()
                    .authorId(user.getId())
                    .relatedGuildId(event.getGuild().getId())
                    .type(rule.getType())
                    .dateSeconds(Punishment.parseDate(now))
                    .endSeconds(rule.getTimeInSeconds() == -1 ? -1 :
                            Punishment.parseDate(new Date(now.getTime() + rule.getTimeInSeconds() * 1000)))
                    .reason(name)
                    .proof(proof)
                    .nsfw(rule.isNsfw())
                    .targetId(embed.getTargetId());


            Bot.getMessageById(event.getTextChannel().getId(), messageId).delete().complete();
            punishmentEmbeds.remove(messageId);
            Bot.getInstance().getPunishmentManager().apply(punishment.build());
        }
    }

    @Data
    private class PunishmentRequest {
        private final String userId;
        private final String targetId;
        private final String proof;
    }
}
