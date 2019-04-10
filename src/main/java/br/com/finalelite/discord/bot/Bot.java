package br.com.finalelite.discord.bot;

import br.com.finalelite.discord.bot.command.CommandHandler;
import br.com.finalelite.bots.main.command.commands.moderation.*;
import br.com.finalelite.bots.main.command.commands.server.*;
import br.com.finalelite.bots.main.command.commands.support.*;
import br.com.finalelite.discord.bot.command.commands.support.utils.TicketLogger;
import br.com.finalelite.bots.main.command.commands.utils.*;
import br.com.finalelite.discord.bot.command.commands.utils.*;
import br.com.finalelite.discord.bot.listeners.JoinListener;
import br.com.finalelite.bots.main.utils.*;
import br.com.finalelite.discord.bot.command.commands.moderation.*;
import br.com.finalelite.discord.bot.command.commands.server.*;
import br.com.finalelite.discord.bot.command.commands.support.*;
import br.com.finalelite.discord.bot.utils.*;
import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class Bot {

    @Getter
    // singleton
    private static Bot instance;
    @Getter
    // the api
    private JDA jda;
    @Getter
    // the bot config
    // (there's a lot of things in the config, like the token, the bot owner, etc)
    private Config config;
    @Getter
    // the database
    // (SQL, using EzSQL as API)
    private Database database;
    @Getter
    // a simple command handler
    private CommandHandler commandHandler;
    @Getter
    // a captcha builder
    // (used to verify user account)
    private CaptchaManager captcha = new CaptchaManager();
    @Getter
    // a map of the Catpcha channel id and the time epoch of the channel creation
    // (used to delete old channels)
    private Map<String, Integer> captchaChannels = new HashMap<>();
    // the ticket transcriptor
    @Getter
    private TicketLogger ticketLogger = new TicketLogger();

    public Bot() {
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

        // try to connect to Discord
        try {
            jda = new JDABuilder(config.getToken()).build().awaitReady();
            SimpleLogger.log("Logged.");

            // check if the bot is in our guild and only in our guild
            if (jda.getGuilds().size() == 0)
                System.out.printf("Invite-me for a server: https://discordapp.com/oauth2/authorize?client_id=%s&permissions=8&scope=bot%n", jda.getSelfUser().getId());
            else if (jda.getGuilds().size() > 1)
                shutdown(String.format("The bot is in %d guilds. For security, the bot only run in the official guild.", jda.getGuilds().size()));

            // set the "Playing" status
            jda.getPresence().setGame(config.getPresence().toGame());

            // register some event listeners
            jda.addEventListener(new JoinListener());

            // print some usefull information
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

        // create a command handler with '!' as prefix
        commandHandler = new CommandHandler("!");

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
        commandHandler.registerCommand(new ClearCommand());

        // finalelite
        commandHandler.registerCommand(new VIPCommand());
        commandHandler.registerCommand(new GetUserIdCommand());
        commandHandler.registerCommand(new GetNickCommand());
        commandHandler.registerCommand(new SetNickCommand());
        commandHandler.registerCommand(new GetDiscordCommand());
        commandHandler.registerCommand(new InvoicesCommand());

        // moderation
        commandHandler.registerCommand(new BanCommand());
        commandHandler.registerCommand(new WarnCommand());
        commandHandler.registerCommand(new KickCommand());
        commandHandler.registerCommand(new TempBanCommand());
        commandHandler.registerCommand(new MuteCommand());
        commandHandler.registerCommand(new TempMuteCommand());
        commandHandler.registerCommand(new UnMuteCommand());
        commandHandler.registerCommand(new UnBanCommand());
        commandHandler.registerCommand(new UnWarnCommand());

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
                if (captchaChannels.isEmpty())
                    continue;

                val newList = new HashMap<>(captchaChannels);
                captchaChannels.forEach((channelId, createIn) -> {
                    val c = getJda().getTextChannelById(channelId);
                    if (c == null)
                        return;

                    val now = new Date();
                    if (now.getTime() / 1000 >= createIn + 5 * 60) {
                        c.delete().complete();
                        val channel = jda.getTextChannelById(getConfig().getVerifyChannelId());
                        channel.getGuild().getController()
                                .kick(channel.getGuild().getMemberById(getDatabase()
                                        .getCaptchaUserIdByChannelId(channelId)), "Tempo limite.").complete();
                        getDatabase().setCaptchaStatus(channelId, Captcha.Status.TIMED_OUT);
                        newList.remove(channelId);
                    }
                });
                captchaChannels = newList;
            }
        }).start();
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

}
