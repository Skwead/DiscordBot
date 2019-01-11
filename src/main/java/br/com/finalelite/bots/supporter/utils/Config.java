package br.com.finalelite.bots.supporter.utils;

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
    private final String dateFormat = "HH:mm:ss Z yyyy/MM/dd";
    @Builder.Default
    private final String ownerId = "the owner id";
    @Builder.Default
    private final String staffRoleId = "staff role id";
    @Builder.Default
    private final String adminRoleId = "major staff role id";
    @Builder.Default
    private final String modLogId = "moderation log channel id";
    @Builder.Default
    private final String welcomeMessage = "Welcome to the party!";
    @Builder.Default
    private final String supportChannelId = "support channel id";
    @Builder.Default
    private final String captchaCategoryId = "captchas category id";
    @Builder.Default
    private final String verifiedRoleId = "verified role id";
    @Builder.Default
    private final String verifyChannelId = "verified channel id";
    @Builder.Default
    private final String staffChannelId = "staff role id";
    @Builder.Default
    private final String mutedRoleId = "muted role id";
    @Builder.Default
    private final String supportCategoryId = "support category id";
    @Builder.Default
    private final String closedCategoryId = "closed tickets category id";
    @Builder.Default
    private final String vipTitanId = "vip role id";
    @Builder.Default
    private final String vipDuqueId = "vip role id";
    @Builder.Default
    private final String vipLordId = "vip role id";
    @Builder.Default
    private final String vipCondeId = "vip role id";
    @Builder.Default
    private final Presence presence = new Presence(Game.GameType.DEFAULT, "Here to help", "https://finalelite.com.br");
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
    private final String sqlDatabase = "supporter";

    public static ConfigBuilder builder() {
        return new ConfigBuilder();
    }

    private static Map<String, String> getDefaultMessages() {
        val map = new HashMap<String, String>();
        map.put("welcome", "Welcome, {user_mention}, what can we do for you today?");
        map.put("bye", "Bye, {user_mention}, we're to help.");
        return map;
    }
}
