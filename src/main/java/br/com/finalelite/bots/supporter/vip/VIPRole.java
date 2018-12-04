package br.com.finalelite.bots.supporter.vip;

import br.com.finalelite.bots.supporter.Main;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum VIPRole {
    TITAN(Main.getConfig().getVipTitanId()),
    DUQUE(Main.getConfig().getVipDuqueId()),
    LORD(Main.getConfig().getVipLordId()),
    CONDE(Main.getConfig().getVipCondeId());

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
