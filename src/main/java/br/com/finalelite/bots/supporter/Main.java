package br.com.finalelite.bots.supporter;

import br.com.finalelite.bots.supporter.command.CommandHandler;
import br.com.finalelite.bots.supporter.command.commands.*;
import br.com.finalelite.bots.supporter.command.commands.messages.MsgCommand;
import br.com.finalelite.bots.supporter.command.commands.messages.MsgConfigCommand;
import br.com.finalelite.bots.supporter.utils.Config;
import br.com.finalelite.bots.supporter.utils.ConfigManager;
import br.com.finalelite.bots.supporter.utils.Database;
import br.com.finalelite.bots.supporter.utils.Presence;
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
import java.util.HashMap;

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
        // create the config if not exists
        val file = new File("config.json");
        if (!file.exists()) {
            val messages = new HashMap<String, String>();
            messages.put("welcome", "Hello ${user-mention}, how can we help you today?");
            messages.put("bye", "Thank you ${user-mention}, we're here to help.");
            val defaultConfig = Config.builder()
                    .token("your token")
                    .ownerId("999123")
                    .presence(new Presence(Game.GameType.DEFAULT, "Here to help", "https://www.youtube.com/watch?v=dQw4w9WgXcQ"))
                    .welcomeMessage("Welcome to the party.")
                    .supportChannelId("32123")
                    .messages(messages)
                    .staffChannelId("31231123")
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
            ConfigManager.saveConfigToFile(defaultConfig);
            System.out.println("Default config file created, please, configure and run the bot again.");
            System.exit(0);
        }

        // l o a d t h e c o n f i g
        loadConfig();
        // belive you or not

        // connect to the db
        db = new Database(config.getSqlAddress(), config.getSqlPort(), config.getSqlUsername(), config.getSqlPassword(), config.getSqlDatabase());
        try {
            db.connect();
            System.out.println("Connected to MySQL.");
        } catch (SQLException | ClassNotFoundException e) {
            // or not
            System.out.println("Cannot connect to database.");
            e.printStackTrace();
            System.exit(-3);
        }

        // add a handler, this will send the stacktrace to the owner in the DM
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            throwable.printStackTrace();
            Database.handleException(throwable);
        });

        // create a command handler with '!' as prefix
        commandHandler = new CommandHandler("!");

        // register many commands
        commandHandler.registerCommand(new SayCommand());
        commandHandler.registerCommand(new SupportCommand());
        commandHandler.registerCommand(new DeleteCommand());
        commandHandler.registerCommand(new RenameCommand());
        commandHandler.registerCommand(new SpamCommand());
        commandHandler.registerCommand(new RemoveCommand());
        commandHandler.registerCommand(new HelpCommand());
        commandHandler.registerCommand(new AddCommand());
        commandHandler.registerCommand(new CloseCommand());
        commandHandler.registerCommand(new PrecenseCommand());
        commandHandler.registerCommand(new MsgCommand());
        commandHandler.registerCommand(new VIPCommand());
        commandHandler.registerCommand(new GetUserIdCommand());
        commandHandler.registerCommand(new MsgConfigCommand());
        commandHandler.registerCommand(new GetNickCommand());
        commandHandler.registerCommand(new SetNickCommand());
        commandHandler.registerCommand(new GetDiscordCommand());
        commandHandler.registerCommand(new InvoicesCommand());
        // \o/

        // try to connect to Discord
        try {
            jda = new JDABuilder(config.getToken()).build().awaitReady();
            System.out.println("Logged.");
            if (jda.getGuilds().size() == 0)
                System.out.printf("Invite-me for a server: https://discordapp.com/oauth2/authorize?client_id=%s&permissions=8&scope=bot%n", jda.getSelfUser().getId());
            else if (jda.getGuilds().size() > 1)
                shutdown(String.format("The bot is in %d guilds. For security, the bot only run in the official guild.", jda.getGuilds().size()));
            jda.getPresence().setGame(config.getPresence().toGame());
            jda.addEventListener(new Main());
        } catch (InterruptedException | LoginException e) {
            System.out.println("Cannot login.");
            e.printStackTrace();
            System.exit(-2);
        }

        // add a handler to the exit event, just to send to the bot owner
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown("Exited by user")));
    }


    public static void loadConfig() {
        config = ConfigManager.loadConfigFromFile();
    }

    public static void shutdown(String reason) {
        System.out.printf("Shutting down. %s%n", reason);
        val pv = Main.getJda().getUserById(Main.getConfig().getOwnerId()).openPrivateChannel().complete();
        pv.sendMessage(String.format(":warning: Shutting down your bot: %s", reason)).complete(); // warn the bot owner
        jda.shutdownNow(); // i don't know if this is really necessary, but sounds great
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        // lets block someone of "steal" my bot
        if (jda.getGuilds().size() == 0)
            return; // oh, okay this is the first guild
        // grr, seems like someone else get the bot invite
        shutdown(String.format("The bot is in %d guilds. For security, the bot only run in the official guild.", jda.getGuilds().size()));
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        // send a welcome message to the new user :)
        val user = event.getUser();
        val pv = event.getUser().openPrivateChannel().complete();
        if (pv == null) // i don't know if this is possible, but lets check
            return;

        if (config.getWelcomeMessage() == null) // oh, there's no welcome message :(
            return;

        try {
            // get the user picture and put in the server's image
            val connection = (HttpURLConnection) new URL(user.getAvatarUrl()).openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/4.76"); // avoid 403 errors
            val userImage = ImageIO.read(connection.getInputStream()); // get the user avatar
            val baseImage = ImageIO.read(Main.class.getResourceAsStream("/image.png")); // get the base image
            val image = new BufferedImage(906, 398, BufferedImage.TYPE_INT_ARGB); // create some place to build the image
            val graphic = image.getGraphics();
            graphic.drawImage(userImage, 367, 26, 178, 178, null); // draw the user avatar
            graphic.drawImage(baseImage, 0, 0, null); // draw the base image
            val bytes = new ByteArrayOutputStream();
            ImageIO.write(image, "png", bytes); // get the bytes
            pv.sendFile(bytes.toByteArray(), "welcome.png", new MessageBuilder(config.getWelcomeMessage()).build()).complete(); // send
        } catch (IOException e) {
            // >:( we cannot send the image, so lets just send the message
            pv.sendMessage(new MessageBuilder(config.getWelcomeMessage()).build()).complete();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        val message = event.getMessage();
        val channel = message.getChannel();
        val author = message.getAuthor();
        val textChannel = event.getTextChannel();

        if (author.getId().equals(jda.getSelfUser().getId())) // comment this line if you're a "s a d b o y"
            return; // this one too

        if (channel.getType() == ChannelType.PRIVATE) {
            // lets disable message in the DM
            channel.sendMessage("Não respondo via DM ainda, por isso, utilize o chat <#" + config.getSupportChannelId() + "> para executar os comandos.").complete();
            return;
        }

        // okay, if it's not a private channel, so its a text channel, right?
        val parent = textChannel.getParent();

        // check if there's a parent category
        if (parent == null)
            return;

        // check if the bot have to handle something in this channel
        // the bot have to handle things in the support channel, in the staff channel (used by staff to test the bot), in the main category and in the closed category
        if ((!channel.getId().equals(config.getSupportChannelId()) && !channel.getId().equals(config.getStaffChannelId())) &&
                (!parent.getId().equals(config.getCategoryId()) && !parent.getId().equals(config.getClosedCategoryId())))
            return;

        // okay, lets handle the command. If this is a invalid command and it's executed in the support channel, delete this spam message
        if (!commandHandler.handle(event) && channel.getId().equals(config.getSupportChannelId()))
            message.delete().complete();
    }

}
