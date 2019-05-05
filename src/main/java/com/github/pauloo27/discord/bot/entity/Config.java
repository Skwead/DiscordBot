package com.github.pauloo27.discord.bot.entity;

import com.github.pauloo27.discord.bot.entity.punishment.PunishmentType;
import com.github.pauloo27.discord.bot.entity.punishment.Rule;
import com.github.pauloo27.discord.bot.utils.time.TimeUnits;
import lombok.Builder;
import lombok.Data;
import lombok.val;
import net.dv8tion.jda.core.entities.Game;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Config {
    @Builder.Default
    private final String token = "your token";

    @Builder.Default
    private final String botName = "Hello World";

    @Builder.Default
    private final String botURL = "https://example.com";

    @Builder.Default
    private final String appealMessage = "Appeal in `support@example.com`.";

    @Builder.Default
    private final String dateFormat = "HH:mm:ss Z yyyy/MM/dd";

    @Builder.Default
    private final Map<String, Rule> rules = getDefaultRules();

    @Builder.Default
    private final String ownerId = "owner's id";

    @Builder.Default
    private final String supportRoleId = "support's role id";

    @Builder.Default
    private final String moderatorRoleId = "moderator's role id";

    @Builder.Default
    private final String adminRoleId = "admin's role id";

    @Builder.Default
    private final String managerRoleId = "manager role's id";

    @Builder.Default
    private final String masterRoleId = "master role's id";

    @Builder.Default
    private final String modLogId = "moderation's log channel id";

    @Builder.Default
    private final String welcomeMessage = "Welcome to the party!";

    @Builder.Default
    private final String supportChannelId = "support's channel id";

    @Builder.Default
    private final String captchaCategoryId = "captchas' category id";

    @Builder.Default
    private final String verifiedRoleId = "verified role's id";

    @Builder.Default
    private final String verifyChannelId = "verified channel's id";

    @Builder.Default
    private final String staffChannelId = "staff channel's id";

    @Builder.Default
    private final String mutedRoleId = "muted's role id";

    @Builder.Default
    private final String openedCategoryId = "support's category id";

    @Builder.Default
    private final String closedCategoryId = "closed tickets category's id";

    @Builder.Default
    private final String imgurClientId = "imgur api client's id";

    @Builder.Default
    private final Presence presence = new Presence(Game.GameType.DEFAULT, "Here to help", "https://example.com");

    @Builder.Default
    private final Map<String, String> messages = getDefaultMessages();

    @Builder.Default
    private final String sqlAddress = "localhost";

    @Builder.Default
    private final int sqlPort = 3306;

    @Builder.Default
    private final String sqlUsername = "root";

    @Builder.Default
    private final String sqlPassword = "123456seven";

    @Builder.Default
    private final String sqlDatabase = "bot";

    public static ConfigBuilder builder() {
        return new ConfigBuilder();
    }

    // TODO FIX ME PLZ
    private static Map<String, String> getDefaultMessages() {
        val map = new HashMap<String, String>();
        map.put("welcome", "Welcome, ${user_mention}, what can we do for you today?");
        map.put("bye", "Bye, ${user_mention}, we're here to help.");
        return map;
    }

    private static Map<String, Rule> getDefaultRules() {
        val rules = new HashMap<String, Rule>();

        rules.put("Spam", new Rule("Mensagens repetitivas ou inrritantes.", PunishmentType.WARN, -1, false, "\uD83D\uDCE7"));

        rules.put("Ofensa", new Rule("Desrespeitar algum membro do grupo.", PunishmentType.TEMP_MUTE,
                (long) TimeUnits.DAYS.convert(1, TimeUnits.SECONDS), false, "\uD83D\uDD95"));

        rules.put("Doxing", new Rule("Extração e/ou divulgação de informações privadas.", PunishmentType.BAN, -1, false, "\uD83D\uDC41"));

        rules.put("Divulgação", new Rule("Divulgação de serviços ou produto não solicitados.", PunishmentType.BAN, -1, false, "‼"));

        return rules;
    }
}
