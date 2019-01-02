package br.com.finalelite.bots.supporter.command.commands.relations;

import br.com.finalelite.bots.supporter.command.Command;
import br.com.finalelite.bots.supporter.command.CommandPermission;
import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.core.entities.*;

public class LinkAccountCommand extends Command {

    @Getter
    private RelationsRepository repository;

    public LinkAccountCommand(RelationsRepository repository) {
        super(
                "vincular",
                "Gera um código de vinculação da conta do Discord com o Minecraft.",
                CommandPermission.EVERYONE,
                true,
                true,
                true,
                true
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
