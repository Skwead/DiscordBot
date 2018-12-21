package br.com.finalelite.bots.supporter.vip;

import br.com.finalelite.bots.supporter.Supporter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum VIPRole {
    TITAN(Supporter.getInstance().getConfig().getVipTitanId()),
    DUQUE(Supporter.getInstance().getConfig().getVipDuqueId()),
    LORD(Supporter.getInstance().getConfig().getVipLordId()),
    CONDE(Supporter.getInstance().getConfig().getVipCondeId());

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
