package br.com.finalelite.bots.supporter.utils;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class Config {
    private final String token;
    private final String ownerId;
    private final String staffRoleId;
    private final String adminRoleId;
    private final String welcomeMessage;
    private final String supportChannelId;
    private final String captchaCategoryId;
    private final String verifiedRoleId;
    private final String verifyChannelId;
    private final String staffChannelId;
    private final String categoryId;
    private final String vipTitanId;
    private final String vipDuqueId;
    private final String vipLordId;
    private final Presence presence;
    private final String vipCondeId;
    private final Map<String, String> messages;
    private final String closedCategoryId;
    private final String sqlAddress;
    private final int sqlPort;
    private final String sqlUsername;
    private final String sqlPassword;
    private final String sqlDatabase;

    public static ConfigBuilder builder() {
        return new ConfigBuilder();
    }
}
