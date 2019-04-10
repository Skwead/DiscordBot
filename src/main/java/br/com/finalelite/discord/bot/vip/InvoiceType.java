package br.com.finalelite.discord.bot.vip;


public enum InvoiceType {
    ONE_MONTH,
    TWO_MONTHS,
    ETERNAL;

    public static InvoiceType fromId(byte id) {
        return InvoiceType.values()[id];
    }

    public String toPtBR() {
        if (this == ONE_MONTH)
            return "mensal";
        if (this == TWO_MONTHS)
            return "dois meses";
        if (this == ETERNAL)
            return "eterno";
        else
            return null;
    }

}
