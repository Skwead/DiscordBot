package com.github.pauloo27.discord.bot;

import com.github.pauloo27.discord.bot.commands.moderation.*;
import com.github.pauloo27.discord.bot.commands.support.*;
import com.github.pauloo27.discord.bot.commands.utils.*;
import com.github.pauloo27.discord.bot.entity.Config;
import com.github.pauloo27.discord.bot.listeners.JoinListener;
import com.github.pauloo27.discord.bot.manager.*;
import com.github.pauloo27.discord.bot.utils.SimpleLogger;
import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;

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
    private ConfigManager configManager;
    @Getter
    // the database
    // (SQL, using EzSQL as API)
    private DatabaseManager database;
    @Getter
    // a simple command handler
    private CommandManager commandManager;
    @Getter
    // a captchaManager builder
    // (used to verify user account)
    private CaptchaManager captchaManager = new CaptchaManager();
    @Getter
    private PunishmentManager punishmentManager;
    @Getter
    private ImgurManager imgurManager;

    public Bot() {
        instance = this;

        configManager = new ConfigManager();

        val config = getConfig();

        connectToDatabase(config);

        tryConnectToDiscord(config);

        setupFallbacks();

        punishmentManager = new PunishmentManager();

        registerCommands();

        imgurManager = new ImgurManager(config.getImgurClientId());
    }

    private void tryConnectToDiscord(Config config) {
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

            printBotInformation();

        } catch (InterruptedException | LoginException e) {
            SimpleLogger.log("Cannot login.");
            e.printStackTrace();
            System.exit(-2);
        }
    }

    private void printBotInformation() {
        SimpleLogger.log("Members: %d", jda.getGuilds().get(0).getMembers().size());
        SimpleLogger.log("Unverified Members: %d", jda.getGuilds().get(0).getMembers().stream()
                .filter(member -> member.getRoles().size() == 0).count());
        SimpleLogger.log("Total channels: %d", jda.getGuilds().get(0).getChannels().size());
        SimpleLogger.log("Text channels: %d", jda.getGuilds().get(0).getTextChannels().size());
        SimpleLogger.log("Voice channels: %d", jda.getGuilds().get(0).getVoiceChannels().size());
        SimpleLogger.log("Roles count: %d", jda.getGuilds().get(0).getRoles().size());
        SimpleLogger.log("Categories count: %d", jda.getGuilds().get(0).getCategories().size());
    }

    private void setupFallbacks() {
        // add a handler to the exit event, just to send to the bot owner
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown("Exited by user")));
        // add a handler, this will send the stacktrace to the owner in the DM
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            throwable.printStackTrace();
            SimpleLogger.sendStackTraceToOwner((Exception) throwable);
        });
    }

    private void connectToDatabase(Config config) {
        database = new DatabaseManager(config.getSqlAddress(), config.getSqlPort(), config.getSqlUsername(), config.getSqlPassword(), config.getSqlDatabase());
        try {
            database.connect();
            SimpleLogger.log("Connected to MySQL.");
        } catch (SQLException | ClassNotFoundException e) {
            // or not
            SimpleLogger.log("Cannot connect to database.");
            e.printStackTrace();
            System.exit(-3);
        }
    }

    public Config getConfig() {
        if (configManager == null)
            return null;
        return configManager.getConfig();
    }

    private void registerCommands() {
        // create a command handler with '!' as prefix
        commandManager = new CommandManager("!");

        /* support */
        commandManager.registerCommand(new AddCommand());
        commandManager.registerCommand(new MsgCommand());
        commandManager.registerCommand(new DeleteCommand());
        commandManager.registerCommand(new RenameCommand());
        commandManager.registerCommand(new MsgConfigCommand());
        commandManager.registerCommand(new SupportCommand());
        commandManager.registerCommand(new CloseCommand());
        commandManager.registerCommand(new RemoveCommand());
        commandManager.registerCommand(new SpamCommand());

        /* utils */
        commandManager.registerCommand(new HelpCommand());
        commandManager.registerCommand(new PingCommand());
        commandManager.registerCommand(new VerifyCommand());
        commandManager.registerCommand(new SayCommand());
        commandManager.registerCommand(new RolesCommand());
        commandManager.registerCommand(new PresenceCommand());
        commandManager.registerCommand(new ClearCommand());

        /* moderation */
        commandManager.registerCommand(new PunishCommand());
        commandManager.registerCommand(new BanCommand());
        commandManager.registerCommand(new WarnCommand());
        commandManager.registerCommand(new KickCommand());
        commandManager.registerCommand(new TempBanCommand());
        commandManager.registerCommand(new MuteCommand());
        commandManager.registerCommand(new TempMuteCommand());
        commandManager.registerCommand(new UnMuteCommand());
        commandManager.registerCommand(new UnBanCommand());
        commandManager.registerCommand(new UnWarnCommand());
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

    public void shutdown(String reason) {
        SimpleLogger.log(String.format("Shutting down. %s", reason));
        SimpleLogger.sendLogToOwner(String.format(":warning: Shutting down your bot: %s", reason));
        jda.shutdownNow(); // i don't know if this is really necessary, but sounds great
    }

}
