package br.com.finalelite.bots.supporter.utils.time;

import java.util.ArrayList;
import java.util.List;

/**
 * A RSpigot class.
 * Part of the time unit API. Singleton of short units.
 *
 * @author Paulo
 * @version 1.0
 */
public class ShortUnits {

    private static List<Unit> units = new ArrayList<>();

    /**
     * Seconds unit.
     */
    public static Unit MILLISECONDS = registerUnit(new Unit("milisegundo", "milisegundo", 0.001, "ms", "millis", "milis"));
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

    protected static Unit registerUnit(Unit unit) {
        units.add(unit);
        return unit;
    }

    public static Unit getUnitByName(String name) {
        return units.stream().filter(unit -> unit.isValid(name)).findFirst().orElse(null);
    }

}
