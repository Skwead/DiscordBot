package br.com.finalelite.bots.supporter.utils.time;

import br.com.finalelite.bots.supporter.utils.time.unit.ShortUnit;

/**
 * A RSpigot class.
 * Part of the time unit API. Enum of short units.
 *
 * @author Paulo
 * @version 1.0
 */
public enum ShortUnits {
    /**
     * Seconds unit.
     */
    SECONDS(new ShortUnit("segundo", "segundos", 1, "second", "seconds", "seg", "secs", "s")),
    /**
     * Minutes unit.
     */
    MINUTES(new ShortUnit("minuto", "minutos", 60, "minute", "minutes", "min", "m")),
    /**
     * Hours unit.
     */
    HOURS(new ShortUnit("hora", "horas", 3600, "hour", "hours", "h"));

    private ShortUnit unit;

    ShortUnits(ShortUnit unit) {
        this.unit = unit;
    }

    /**
     * Gets the unit instance.
     *
     * @return The unit instance
     */
    public ShortUnit getUnit() {
        return unit;
    }

}
