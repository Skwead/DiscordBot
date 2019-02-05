package br.com.finalelite.bots.supporter;

import br.com.finalelite.bots.supporter.command.CommandHandler;
import br.com.finalelite.bots.supporter.command.commands.moderation.BanCommand;
import br.com.finalelite.bots.supporter.command.commands.moderation.KickCommand;
import br.com.finalelite.bots.supporter.command.commands.moderation.MuteCommand;
import br.com.finalelite.bots.supporter.command.commands.moderation.TempBanCommand;
import br.com.finalelite.bots.supporter.command.commands.server.*;
import br.com.finalelite.bots.supporter.command.commands.support.*;
import br.com.finalelite.bots.supporter.command.commands.support.MsgCommand;
import br.com.finalelite.bots.supporter.command.commands.support.MsgConfigCommand;
import br.com.finalelite.bots.supporter.command.commands.utils.*;
import br.com.finalelite.bots.supporter.command.commands.utils.VerifyCommand;
import br.com.finalelite.bots.supporter.utils.*;
import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
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
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class Supporter extends ListenerAdapter {

    @Getter
    private static Supporter instance;
    @Getter
    private JDA jda;
    @Getter
    private Config config;
    @Getter
    private Database database;
    @Getter
    private CommandHandler commandHandler;
    @Getter
    private Captcha captcha = new Captcha();
    @Getter
    private Map<String, Integer> channelsToRemove = new HashMap<>();

    public Supporter() {
        instance = this;
        // create the config if not exists
        val file = new File("config.json");
        if (!file.exists()) {
            val defaultConfig = Config.builder().build();
            ConfigManager.saveConfigToFile(defaultConfig);
            SimpleLogger.log("Default config file created, please, configure and run the bot again.");
            System.exit(0);
        }

        // l o a d t h e c o n f i g
        loadConfig();
        // believe you or not

        // check if the config is missing something
        val nullValues = checkConfig();
        if (!nullValues.isEmpty()) {
            SimpleLogger.log("Cannot find `%s` in the config.",
                    nullValues.stream().map(Field::getName).collect(Collectors.joining(", ")));
            SimpleLogger.log("Please, fix the config before run the bot.");
            System.exit(0);
        }

        // connect to the database
        database = new Database(config.getSqlAddress(), config.getSqlPort(), config.getSqlUsername(), config.getSqlPassword(), config.getSqlDatabase());
        try {
            database.connect();
            SimpleLogger.log("Connected to MySQL.");
        } catch (SQLException | ClassNotFoundException e) {
            // or not
            SimpleLogger.log("Cannot connect to database.");
            e.printStackTrace();
            System.exit(-3);
        }

        // add a handler, this will send the stacktrace to the owner in the DM
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            throwable.printStackTrace();
            SimpleLogger.sendStackTraceToOwner((Exception) throwable);
        });

        // create a command handler with '!' as prefix
        commandHandler = new CommandHandler("!");

        // try to connect to Discord
        try {
            jda = new JDABuilder(config.getToken()).build().awaitReady();
            SimpleLogger.log("Logged.");
            if (jda.getGuilds().size() == 0)
                System.out.printf("Invite-me for a server: https://discordapp.com/oauth2/authorize?client_id=%s&permissions=8&scope=bot%n", jda.getSelfUser().getId());
            else if (jda.getGuilds().size() > 1)
                shutdown(String.format("The bot is in %d guilds. For security, the bot only run in the official guild.", jda.getGuilds().size()));
            jda.getPresence().setGame(config.getPresence().toGame());
            jda.addEventListener(this);
            SimpleLogger.log("Members: %d", jda.getGuilds().get(0).getMembers().size());
            SimpleLogger.log("Unverified Members: %d", jda.getGuilds().get(0).getMembers().stream()
                    .filter(member -> member.getRoles().size() == 0).count());
            SimpleLogger.log("Total channels: %d", jda.getGuilds().get(0).getChannels().size());
            SimpleLogger.log("Text channels: %d", jda.getGuilds().get(0).getTextChannels().size());
            SimpleLogger.log("Voice channels: %d", jda.getGuilds().get(0).getVoiceChannels().size());
            SimpleLogger.log("Roles count: %d", jda.getGuilds().get(0).getRoles().size());
            SimpleLogger.log("Categories count: %d", jda.getGuilds().get(0).getCategories().size());
        } catch (InterruptedException | LoginException e) {
            SimpleLogger.log("Cannot login.");
            e.printStackTrace();
            System.exit(-2);
        }
        // add a handler to the exit event, just to send to the bot owner
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown("Exited by user")));

        // register many commands

        // support
        commandHandler.registerCommand(new AddCommand());
        commandHandler.registerCommand(new MsgCommand());
        commandHandler.registerCommand(new DeleteCommand());
        commandHandler.registerCommand(new RenameCommand());
        commandHandler.registerCommand(new MsgConfigCommand());
        commandHandler.registerCommand(new SupportCommand());
        commandHandler.registerCommand(new CloseCommand());
        commandHandler.registerCommand(new RemoveCommand());
        commandHandler.registerCommand(new SpamCommand());

        // utils
        commandHandler.registerCommand(new HelpCommand());
        commandHandler.registerCommand(new PingCommand());
        commandHandler.registerCommand(new VerifyCommand());
        commandHandler.registerCommand(new SayCommand());
        commandHandler.registerCommand(new RolesCommand());
        commandHandler.registerCommand(new PresenceCommand());

        // finalelite
        commandHandler.registerCommand(new VIPCommand());
        commandHandler.registerCommand(new GetUserIdCommand());
        commandHandler.registerCommand(new GetNickCommand());
        commandHandler.registerCommand(new SetNickCommand());
        commandHandler.registerCommand(new GetDiscordCommand());
        commandHandler.registerCommand(new InvoicesCommand());

        // moderation
        commandHandler.registerCommand(new BanCommand());
        commandHandler.registerCommand(new KickCommand());
        commandHandler.registerCommand(new TempBanCommand());
        commandHandler.registerCommand(new MuteCommand());

        // command disabled until the myocardium is released
        // commandHandler.registerCommand(new LinkAccountCommand(new RelationsRepository(jda)));


        // thread to auto close idle captchas
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000 * 20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (channelsToRemove.isEmpty())
                    continue;

                val newList = new HashMap<>(channelsToRemove);
                channelsToRemove.forEach((channelId, createIn) -> {
                    val c = getJda().getTextChannelById(channelId);
                    if (c == null)
                        return;

                    val now = new Date();
                    if (now.getTime() / 1000 >= createIn + 5 * 60) {
                        c.delete().complete();
                        val channel = jda.getTextChannelById(getConfig().getVerifyChannelId());
                        channel.getGuild().getController().kick(channel.getGuild().getMemberById(getDatabase().getCaptchaUserIdByChannelId(channelId)), "Tempo limite.").complete();
                        getDatabase().setCaptchaStatus(channelId, (byte) -3);
                        newList.remove(channelId);
                    }
                });
                channelsToRemove = newList;
            }
        }).start();
    }

    public void loadConfig() {
        config = ConfigManager.loadConfigFromFile();
    }

    public List<Field> checkConfig() {
        val fields = config.getClass().getDeclaredFields();
        return Arrays.stream(fields).filter(field -> {
            try {
                field.setAccessible(true);
                return field.get(config) == null;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return false;
        }).collect(Collectors.toList());
    }

    public void shutdown(String reason) {
        SimpleLogger.log(String.format("Shutting down. %s", reason));
        SimpleLogger.sendLogToOwner(String.format(":warning: Shutting down your bot: %s", reason));
        jda.shutdownNow(); // i don't know if this is really necessary, but sounds great
    }

    public static Role getRoleById(String id) {
        return getInstance().getJda().getRoleById(id);
    }

    public static User getUserById(String id) {
        return getInstance().getJda().getUserById(id);
    }

    public static Member getMemberById(String guildId, String memberId) {
        return getGuildById(guildId).getMemberById(memberId);
    }

    public static Guild getGuildById(String id) {
        return getInstance().getJda().getGuildById(id);
    }

    public static TextChannel getTextChannelById(String id) {
        return getInstance().getJda().getTextChannelById(id);
    }

    public static Message getMessageById(String channelId, String messageId) {
        return getInstance().getJda().getTextChannelById(channelId).getMessageById(messageId).complete();
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
            val baseImage = ImageIO.read(Supporter.class.getResourceAsStream("/image.png")); // get the base image
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
            channel.sendMessage(String.format("Não respondo via DM ainda, utilize o chat <#%s> para executar os comandos.", config.getSupportChannelId())).complete();
            return;
        }

        // okay, lets handle the command. If this is a invalid command and it's executed in the support channel, delete this spam message
        if (!commandHandler.handle(event) && channel.getId().equals(config.getSupportChannelId())) {
            message.delete().complete();
        }
    }

}
