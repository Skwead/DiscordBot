package com.github.pauloo27.discord.bot.entity.vip;


import lombok.Getter;

@Getter
public class Invoice {

    private final long id;
    private final long userId;
    private final boolean paid;
    private final VIPRole vip;
    private final InvoiceType type;

    public Invoice(long id, long userId, int price, boolean paid) {
        this.id = id;
        this.userId = userId;
        this.paid = paid;

        switch (price) {
            // TITAN
            case 15:
                vip = VIPRole.TITAN;
                type = InvoiceType.ONE_MONTH;
                break;
            case 16:
                vip = VIPRole.TITAN;
                type = InvoiceType.TWO_MONTHS;
                break;
            case 17:
                vip = VIPRole.TITAN;
                type = InvoiceType.ETERNAL;
                break;
            // Duque
            case 12:
                vip = VIPRole.DUQUE;
                type = InvoiceType.ONE_MONTH;
                break;
            case 13:
                vip = VIPRole.DUQUE;
                type = InvoiceType.TWO_MONTHS;
                break;
            case 14:
                vip = VIPRole.DUQUE;
                type = InvoiceType.ETERNAL;
                break;
            // Lord
            case 6:
                vip = VIPRole.LORD;
                type = InvoiceType.ONE_MONTH;
                break;
            case 9:
                vip = VIPRole.LORD;
                type = InvoiceType.TWO_MONTHS;
                break;
            case 11:
                vip = VIPRole.LORD;
                type = InvoiceType.ETERNAL;
                break;
            // Conde
            case 7:
                vip = VIPRole.CONDE;
                type = InvoiceType.ONE_MONTH;
                break;
            case 8:
                vip = VIPRole.CONDE;
                type = InvoiceType.TWO_MONTHS;
                break;
            case 10:
                vip = VIPRole.CONDE;
                type = InvoiceType.ETERNAL;
                break;

            default:
                vip = null;
                type = null;
        }
    }

}
