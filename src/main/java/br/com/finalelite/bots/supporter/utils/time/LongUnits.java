package br.com.finalelite.bots.supporter.utils.time;

/**
 * A RSpigot class.
 * Part of the time unit API. Singleton of long units.
 *
 * @author Paulo
 * @version 1.0
 */
public class LongUnits extends ShortUnits {
    /**
     * Days unit.
     */
    public static Unit DAYS = registerUnit(new Unit("dia", "dias", 86400, "day", "days", "d"));
    /**
     * Weeks unit.
     */
    public static Unit WEEKS = registerUnit(new Unit("semana", "semanas", 604800, "week", "weeks", "sem", "w"));
    /**
     * Months unit.
     */
    public static Unit MONTHS = registerUnit(new Unit("mês", "meses", 2629800, "month", "months", "mesês", "n"));
    /**
     * Years unit.
     */
    public static Unit YEARS = registerUnit(new Unit("ano", "anos", 31557600, "year", "years", "y", "a"));

}
