package com.github.pauloo27.discord.bot.utils.time;

import lombok.val;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A RSpigot class.
 * Part of the time unit API. Singleton of long units.
 *
 * @author Paulo
 * @version 1.0
 */
public class TimeUnits {

    private static List<Unit> units = new LinkedList<>();

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
    public static Unit MONTHS = registerUnit(new Unit("mês", "meses", 2592000, "month", "months", "mes", "mesês", "n"));
    /**
     * Years unit.
     */
    public static Unit YEARS = registerUnit(new Unit("ano", "anos", 31536000, "year", "years", "y", "a"));

    protected static Unit registerUnit(Unit unit) {
        units.add(unit);
        return unit;
    }

    public static Unit getUnitByName(String name) {
        return units.stream().filter(unit -> unit.isValid(name)).findFirst().orElse(null);
    }

    private static void appendUnit(StringBuilder builder, AtomicLong durationInSeconds, Unit unit) {
        long unitValue = (long) (durationInSeconds.get() / unit.getUnitInSeconds());
        val unitInSeconds = (long) unit.getUnitInSeconds();
        if (unitValue >= 1) {
            durationInSeconds.set(durationInSeconds.get() - (unitInSeconds * unitValue));
            builder.append(unit.pluralizeWithAmount(unitValue)).append(" ");
        }
    }

    public static String formatDuration(long durationInSeconds) {
        val duration = new AtomicLong(durationInSeconds);
        val builder = new StringBuilder();

        val units = new LinkedList<>(TimeUnits.units);
        Collections.reverse(units);

        units.forEach(unit ->
                TimeUnits.appendUnit(builder, duration, unit));

        return builder.toString().trim();
    }

}
