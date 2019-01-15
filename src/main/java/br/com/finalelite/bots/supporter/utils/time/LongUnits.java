package br.com.finalelite.bots.supporter.utils.time;

import br.com.finalelite.bots.supporter.utils.time.unit.LongUnit;

/**
 * A RSpigot class.
 * Part of the time unit API. Enum of long units.
 *
 * @author Paulo
 * @version 1.0
 */
public enum LongUnits {
    /**
     * Days unit.
     */
    DAYS(new LongUnit("dia", "dias", 86400, "day", "days", "d")),
    /**
     * Weeks unit.
     */
    WEEKS(new LongUnit("semana", "semanas", 604800, "week", "weeks", "sem", "w")),
    /**
     * Months unit.
     */
    MONTHS(new LongUnit("mês", "meses", 2629800, "month", "months", "mesês", "n")),
    /**
     * Years unit.
     */
    YEARS(new LongUnit("ano", "anos", 31557600, "year", "years", "y", "a"));

    private LongUnit unit;

    LongUnits(LongUnit unit) {
        this.unit = unit;
    }

    /**
     * Gets the unit instance.
     *
     * @return The unit instance
     */
    public LongUnit getUnit() {
        return unit;
    }

}
