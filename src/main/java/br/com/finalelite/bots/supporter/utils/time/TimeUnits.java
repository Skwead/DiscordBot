package br.com.finalelite.bots.supporter.utils.time;

import java.util.ArrayList;
import java.util.List;

/**
 * A RSpigot class.
 * Part of the time unit API. Singleton of long units.
 *
 * @author Paulo
 * @version 1.0
 */
public class TimeUnits {

    private static List<Unit> units = new ArrayList<>();

    /**
     * Seconds unit.
     */
    public static Unit MILLISECONDS = registerUnit(new Unit("milisegundo", "milisegundos", 0.001, "ms", "millis", "milis"));
    /**
     * Seconds unit.
     */
    public static Unit SECONDS = registerUnit(new Unit("segundo", "segundos", 1, "second", "seconds", "seg", "secs", "s"));
    /**
     * Minutes unit.
     */
    public static Unit MINUTES = registerUnit(new Unit("minuto", "minutos", 60, "minute", "minutes", "min", "m"));
    /**
     * Hours unit.
     */
    public static Unit HOURS = registerUnit(new Unit("hora", "horas", 3600, "hour", "hours", "h"));

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
    public static Unit MONTHS = registerUnit(new Unit("mês", "meses", 2629800, "month", "months", "mes", "mesês", "n"));
    /**
     * Years unit.
     */
    public static Unit YEARS = registerUnit(new Unit("ano", "anos", 31557600, "year", "years", "y", "a"));

    protected static Unit registerUnit(Unit unit) {
        System.out.println("Registerning Unit " + unit.getName());
        System.out.println("Current units: " + units);
        units.add(unit);
        return unit;
    }

    public static Unit getUnitByName(String name) {
        return units.stream().filter(unit -> unit.isValid(name)).findFirst().orElse(null);
    }

}
