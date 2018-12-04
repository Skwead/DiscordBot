package br.com.finalelite.bots.supporter;

import br.com.finalelite.bots.supporter.command.CommandHandler;
import br.com.finalelite.bots.supporter.command.commands.*;
import br.com.finalelite.bots.supporter.command.commands.commands.MsgCommand;
import br.com.finalelite.bots.supporter.utils.Config;
import br.com.finalelite.bots.supporter.utils.Database;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;

public class Main extends ListenerAdapter {

    @Getter
    private static JDA jda;
    @Getter
    private static Config config;
    @Getter
    private static Database db;
    @Getter
    private static CommandHandler commandHandler;

    public static void main(String[] args) {
        val file = new File("config.json");
        if (!file.exists()) {
            try {
                file.createNewFile();
                val defaultConfig = Config.builder()
                        .token("your token")
                        .supportChannelId("32123")
                        .categoryId("12345")
                        .closedCategoryId("12345")
                        .staffRoleId("123")
                        .vipTitanId("1234")
                        .vipDuqueId("123123")
                        .vipLordId("123455")
                        .vipCondeId("123123")
                        .sqlAddress("localhost")
                        .sqlPort(3306)
                        .sqlUsername("root")
                        .sqlPassword("1234")
                        .sqlDatabase("database")
                        .build();
                val writer = Files.newWriter(file, Charsets.UTF_8);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                writer.write(gson.toJson(defaultConfig));
                writer.close();
                System.out.println("Default config file created, please, configure and run the bot again.");
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            config = new Gson().fromJson(String.join("\n", Files.readLines(file, Charsets.UTF_8)), Config.class);
        } catch (IOException e) {
            System.out.println("Cannot load the config file.");
            e.printStackTrace();
            System.exit(-1);
        }

        db = new Database(config.getSqlAddress(), config.getSqlPort(), config.getSqlUsername(), config.getSqlPassword(), config.getSqlDatabase());
        try {
            db.connect();
            System.out.println("Connected to MySQL.");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Cannot connect to database.");
            e.printStackTrace();
            System.exit(-3);
        }

        commandHandler = new CommandHandler("!");

        commandHandler.registerCommand(new SayCommand());
        commandHandler.registerCommand(new SupportCommand());
        commandHandler.registerCommand(new DeleteCommand());
        commandHandler.registerCommand(new RenameCommand());
        commandHandler.registerCommand(new SpamCommand());
        commandHandler.registerCommand(new CloseCommand());
        commandHandler.registerCommand(new MsgCommand());
        commandHandler.registerCommand(new VIPCommand());
        commandHandler.registerCommand(new UserIdCommand());
        commandHandler.registerCommand(new InvoicesCommand());

        try {
            jda = new JDABuilder(config.getToken()).build().awaitReady();
            System.out.println("Logged.");
            if (jda.getGuilds().size() == 0)
                System.out.printf("Invite-me for a server: https://discordapp.com/oauth2/authorize?client_id=%s&permissions=8&scope=bot%n", jda.getSelfUser().getId());
            else if (jda.getGuilds().size() > 1) {
                shutdown(String.format("The bot is in %d guilds. For security, the bot only run in the official guild.", jda.getGuilds().size()));
            }
            jda.getPresence().setGame(Game.of(Game.GameType.WATCHING, "vídeos do Willzy enquanto ajudo os jogadores", "https://finalelite.com.br"));
            jda.addEventListener(new Main());
        } catch (InterruptedException | LoginException e) {
            System.out.println("Cannot login.");
            e.printStackTrace();
            System.exit(-2);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown("Exited by user")));
    }

    public static void shutdown(String reason) {
        System.out.printf("Shutting down. %s%n", reason);
        jda.shutdownNow();
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        if (jda.getGuilds().size() == 0)
            return;
        shutdown(String.format("The bot is in %d guilds. For security, the bot only run in the official guild.", jda.getGuilds().size()));
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        val user = event.getUser();
        val pv = event.getUser().openPrivateChannel().complete();
        if (pv == null)
            return;

        try {
            val connection = (HttpURLConnection) new URL(user.getAvatarUrl()).openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/4.76");
            val userImage = ImageIO.read(connection.getInputStream());
            val baseImage = ImageIO.read(Main.class.getResourceAsStream("/image.png"));
            val image = new BufferedImage(906, 398, BufferedImage.TYPE_INT_ARGB);
            val graphic = image.getGraphics();
            graphic.drawImage(userImage, 367, 26, 178, 178, null);
            graphic.drawImage(baseImage, 0, 0, null);
            val bytes = new ByteArrayOutputStream();
            ImageIO.write(image, "png", bytes);
            pv.sendFile(bytes.toByteArray(), "welcome.png", new MessageBuilder("Olá, seja bem-vindo ao Discord do Final Elite.\n" +
                    //"Leia as <#regras>\n" +
                    "Caso tenha dúvidas ou queira reportar um bug ou um hacker use o <#" + config.getSupportChannelId() + ">.\n\n" +
                    "Para comprar VIP acesse nossa loja https://finalelite.com.br").build()).complete();
        } catch (IOException e) {
            pv.sendMessage(new MessageBuilder("Olá, seja bem-vindo ao Discord do Final Elite.\n" +
                    //"Leia as <#regras>\n" +
                    "Caso tenha dúvidas ou queira reportar um bug ou um hacker use o <#" + config.getSupportChannelId() + ">.\n\n" +
                    "Para comprar VIP acesse nossa loja https://finalelite.com.br").build()).complete();
//            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        val message = event.getMessage();
        val channel = message.getChannel();
        val author = message.getAuthor();
        val textChannel = event.getTextChannel();

        if (author.getId().equals(jda.getSelfUser().getId()))
            return;

        if (channel.getType() == ChannelType.PRIVATE) {
            channel.sendMessage("Não respondo via DM ainda, por isso, utilize o chat <#" + config.getSupportChannelId() + "> para executar os comandos.").complete();
            return;
        }

        val parent = textChannel.getParent();

        if (parent == null)
            return;

        if (!channel.getId().equals(config.getSupportChannelId()) &&
                (!parent.getId().equals(config.getCategoryId()) && !parent.getId().equals(config.getClosedCategoryId())))
            return;

        if (!commandHandler.handle(event) && channel.getId().equals(config.getSupportChannelId()))
            message.delete().complete();
    }

}
