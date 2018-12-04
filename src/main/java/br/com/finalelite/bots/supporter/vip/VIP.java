package br.com.finalelite.bots.supporter.vip;

import lombok.Data;

@Data
public class VIP {
    private final String nickname;
    private final String discordId;
    private final long userId;
    private final long paymentId;
    private final String email;
    private VIPRole vipRole;



}
