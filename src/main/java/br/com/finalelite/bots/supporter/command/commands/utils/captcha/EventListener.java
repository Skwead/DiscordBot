package br.com.finalelite.bots.supporter.command.commands.utils.captcha;

import br.com.finalelite.bots.supporter.Supporter;
import lombok.val;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class EventListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        val message = event.getMessage();
        val channel = message.getChannel();
        val guild = event.getGuild();
        val author = message.getAuthor();
        val supporter = Supporter.getInstance();

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
            supporter.getDatabase().setCaptchaStatus(channel.getId(), (byte) 1);
            supporter.getChannelsToRemove().remove(textChannel.getId());
            textChannel.delete().complete();
        } else {
            val times = supporter.getCaptcha().getTries(author.getId());
            if (times >= 5) {
                guild.getController().kick(guild.getMember(author), "Tentativas esgotadas.").complete();
                supporter.getDatabase().setCaptchaStatus(channel.getId(), (byte) -1);
                supporter.getChannelsToRemove().remove(textChannel.getId());
                textChannel.delete().complete();
                return;
            }
            val imageBytes = supporter.getCaptcha().createAnotherCaptcha(author.getId());
            textChannel.getManager().setSlowmode(times * 5 + 5).complete();
            textChannel.sendFile(imageBytes, "captcha.jpg",
                    new MessageBuilder("Digite e envie o texto na imagem abaixo. Você tem mais " + (5 - times) + " tentativa(s).").build()).complete();
        }
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        val user = event.getUser();
        val supporter = Supporter.getInstance();
        val channelId = supporter.getDatabase().getCaptchaChannelIdByUserId(user.getId());

        if (channelId == null)
            return;

        event.getGuild().getTextChannelById(channelId).delete().complete();
        supporter.getDatabase().setCaptchaStatus(channelId, (byte) -4);
        supporter.getChannelsToRemove().remove(channelId);
    }
}
