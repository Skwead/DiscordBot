package br.com.finalelite.bots.supporter.command.commands.captcha;

import br.com.finalelite.bots.supporter.Supporter;
import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.command.CommandPermission;
import br.com.finalelite.bots.supporter.command.CommandType;
import lombok.val;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.Arrays;
import java.util.Date;

public class VerifyCommand extends Command {

    private final Supporter supporter = Supporter.getInstance();

    public VerifyCommand() {
        super(
                "verificar",
                "verifica a conta do usuário",
                CommandPermission.EVERYONE,
                CommandType.DEFAULT
        );

        supporter.getJda().addEventListener(new EventListener());
        supporter.getJda().getCategoryById(supporter.getConfig().getCaptchaCategoryId())
                .getTextChannels().forEach(textChannel -> {
            supporter.getDatabase().setCaptchaStatus(textChannel.getId(), (byte) -2);
            textChannel.delete().complete();
        });
        val channel = supporter.getJda().getTextChannelById(supporter.getConfig().getVerifyChannelId());
        val messages = channel.getIterableHistory().complete();
        if (!messages.isEmpty())
            channel.deleteMessages(messages).complete();
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {
        val role = guild.getRoleById(supporter.getConfig().getVerifiedRoleId());
        if (guild.getMember(author).getRoles().contains(role))
            return;

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
            supporter.getChannelsToRemove().put(newTextChannel.getId(), (int) (new Date().getTime() / 1000));
            val imageBytes = supporter.getCaptcha().createNewCaptcha(author.getId());
            sendSuccess(textChannel, author, String.format("Resolva o captcha no canal <#%s> para poder acessar o servidor.", channel.getId()), 20);
            newTextChannel.sendFile(imageBytes, "captcha.jpg",
                    new MessageBuilder(author.getAsMention() + "\nDigite e envie o texto na imagem abaixo. Você tem 5 tentativas e 5 minutos.").build()).complete();
        }
        message.delete().complete();
    }
}
