package br.com.finalelite.bots.supporter.vip;

import lombok.Data;

@Data
public class VIP {
    private final String discordId;
    private final Invoice invoice;
}
