package br.com.finalelite.discord.bot.command.commands.utils;

import br.com.finalelite.discord.bot.Bot;
import br.com.finalelite.discord.bot.command.Command;
import br.com.finalelite.discord.bot.command.CommandChannelChecker;
import br.com.finalelite.discord.bot.command.CommandPermission;
import br.com.finalelite.discord.bot.command.DefaultCommandCategory;
import br.com.finalelite.discord.bot.utils.Captcha;
import lombok.val;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.Date;

public class VerifyCommand extends Command {

    private final Bot supporter = Bot.getInstance();

    public VerifyCommand() {
        super(
                "verificar",
                "verifica a conta do usuário",
                CommandPermission.EVERYONE,
                CommandChannelChecker.DISABLE,
                DefaultCommandCategory.UTILS
        );

        supporter.getJda().addEventListener(new EventListener());
        supporter.getJda().getCategoryById(supporter.getConfig().getCaptchaCategoryId())
                .getTextChannels().forEach(textChannel -> {
            supporter.getDatabase().setCaptchaStatus(textChannel.getId(), Captcha.Status.RESTARTED);
            textChannel.delete().complete();
        });
        val channel = supporter.getJda().getTextChannelById(supporter.getConfig().getVerifyChannelId());
        val messages = channel.getIterableHistory().complete();
        if (!messages.isEmpty()) {
            if (messages.size() == 1)
                messages.get(0).delete().complete();
            else
                channel.deleteMessages(messages).complete();
        }

    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        val role = guild.getRoleById(supporter.getConfig().getVerifiedRoleId());
        if (guild.getMember(author).getRoles().contains(role)) {
            message.delete().complete();
            return;
        }

        if (!textChannel.getId().equals(supporter.getConfig().getVerifyChannelId()))
            return;

        val channelId = supporter.getDatabase().getCaptchaChannelIdByUserId(author.getId());
        if (channelId == null) {
            val channel = guild.getController()
                    .createTextChannel("verificar-" + author.getId())
                    .setParent(guild.getCategoryById(supporter.getConfig().getCaptchaCategoryId()))
                    .setSlowmode(5)
                    .complete();

            channel.getManager().sync().complete();

            channel.getManager()
                    .putPermissionOverride(guild.getMember(author),
                            Arrays.asList(Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY, Permission.MESSAGE_WRITE), null).complete();

            supporter.getDatabase().createCaptcha(author.getId(), channel.getId());

            val newTextChannel = guild.getTextChannelById(channel.getId());
            supporter.getCaptchaChannels().put(newTextChannel.getId(), (int) (new Date().getTime() / 1000));
            val imageBytes = supporter.getCaptcha().createNewCaptcha(author.getId());
            sendSuccess(textChannel, author, String.format("Resolva o captcha no canal <#%s> para poder acessar o servidor.", channel.getId()), 20);
            newTextChannel.sendFile(imageBytes, "captcha.jpg",
                    new MessageBuilder(author.getAsMention() + "\nDigite e envie o texto na imagem abaixo. Você tem 5 tentativas e 5 minutos.").build()).complete();
        }
        message.delete().complete();
    }

    public class EventListener extends ListenerAdapter {

        @Override
        public void onMessageReceived(MessageReceivedEvent event) {
            val message = event.getMessage();
            val channel = message.getChannel();
            val guild = event.getGuild();
            val author = message.getAuthor();
            val supporter = Bot.getInstance();

            if (author.getId().equals(supporter.getJda().getSelfUser().getId()) || author.isBot() || author.isFake())
                return;

            if (!channel.getType().isGuild() || channel.getType() != ChannelType.TEXT)
                return;

            if (guild.getMember(author).getRoles().contains(guild.getRoleById(supporter.getConfig().getVerifiedRoleId())))
                return;

            val textChannel = event.getTextChannel();
            val parent = textChannel.getParent();

            if (parent == null || !parent.getId().equals(supporter.getConfig().getCaptchaCategoryId())) {
                if (!message.getContentRaw().equalsIgnoreCase("!verificar"))
                    message.delete().complete();
                return;
            }

            val result = supporter.getCaptcha().check(author.getId(), message.getContentRaw());
            if (result) {
                guild.getController().addRolesToMember(guild.getMember(author), guild.getRoleById(supporter.getConfig().getVerifiedRoleId())).complete();
                supporter.getDatabase().setCaptchaStatus(channel.getId(), Captcha.Status.SUCCESS);
                supporter.getCaptchaChannels().remove(textChannel.getId());
                textChannel.delete().complete();
            } else {
                val times = supporter.getCaptcha().getTries(author.getId());
                if (times >= 5) {
                    guild.getController().kick(guild.getMember(author), "Tentativas esgotadas.").complete();
                    supporter.getDatabase().setCaptchaStatus(channel.getId(), Captcha.Status.TOO_MANY_ATTEMPTS);
                    supporter.getCaptchaChannels().remove(textChannel.getId());
                    textChannel.delete().complete();
                    return;
                }
                val imageBytes = supporter.getCaptcha().createAnotherCaptcha(author.getId());
                textChannel.getManager().setSlowmode(times * 5 + 5).complete();
                textChannel.sendFile(imageBytes, "captcha.jpg",
                        new MessageBuilder(
                                String.format("Digite e envie o texto na imagem abaixo. Você tem mais %d tentativa(s).",
                                        5 - times))
                                .build()).complete();
            }
        }

        @Override
        public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
            val user = event.getUser();
            val supporter = Bot.getInstance();
            val channelId = supporter.getDatabase().getCaptchaChannelIdByUserId(user.getId());

            if (channelId == null)
                return;

            event.getGuild().getTextChannelById(channelId).delete().complete();
            supporter.getDatabase().setCaptchaStatus(channelId, Captcha.Status.GUILD_LEFT);
            supporter.getCaptchaChannels().remove(channelId);
        }
    }

}
