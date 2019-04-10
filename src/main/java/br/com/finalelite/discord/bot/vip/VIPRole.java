package br.com.finalelite.discord.bot.vip;

import br.com.finalelite.discord.bot.Bot;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum VIPRole {
    TITAN(Bot.getInstance().getConfig().getVipTitanId()),
    DUQUE(Bot.getInstance().getConfig().getVipDuqueId()),
    LORD(Bot.getInstance().getConfig().getVipLordId()),
    CONDE(Bot.getInstance().getConfig().getVipCondeId());

    private final String roleId;

    public static VIPRole fromVIPName(String name) {
        try {
            return VIPRole.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static VIPRole fromId(byte id) {
        return VIPRole.values()[id];
    }

}
