package br.com.finalelite.discord.bot.command.commands.server;

import br.com.finalelite.discord.bot.command.Command;
import br.com.finalelite.discord.bot.command.CommandChannelChecker;
import br.com.finalelite.discord.bot.command.CommandPermission;
import br.com.finalelite.discord.bot.command.DefaultCommandCategory;
import br.com.finalelite.discord.bot.command.commands.server.utils.RelationsRepository;
import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.core.entities.*;

public class LinkAccountCommand extends Command {

    @Getter
    private RelationsRepository repository;

    public LinkAccountCommand(RelationsRepository repository) {
        super(
                "vincular",
                "gera um código de vinculação da conta do Discord com o Minecraft",
                CommandPermission.EVERYONE,
                CommandChannelChecker.SUPPORT_CHANNEL_ONLY,
                DefaultCommandCategory.SERVER

        );

        this.repository = repository;
    }

    @Override
    public void run(Message message, Guild guild, TextChannel textChannel, User author, String[] args) {

        message.delete().complete();

        val code = repository.storeGeneratedCode(author.getId());
        PrivateChannel privateChannel = author.openPrivateChannel().complete();

        privateChannel.sendMessage(":globe_with_meridians: _Use o comando_ **/vincular " + code + "** _para vincular suas conta do Discord com a conta do servidor._").submit();
        privateChannel.sendMessage(":warning:  _Esse código possui validade de 5 (cinco) minutos. Após esse tempo, será necessário gerar outro._").submit();


    }
}