package br.com.finalelite.bots.supporter.utils.time;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * A RSpigot class.
 * Part of the time unit API. Instance it to create an time unit.
 *
 * <p>Why don't use {@link java.util.concurrent.TimeUnit}? This class implements portuguese version and plural.</p>
 *
 * @author Paulo
 * @version 1.0
 */
public class Unit {

    private String name;
    private String plural;
    private double unitInSeconds;
    private List<String> aliases = new ArrayList<>();

    /**
     * Builds a new time unit.
     *
     * @param name          The unit name
     * @param plural        The unit name in plural
     * @param unitInSeconds 1 of this unit in seconds
     * @param aliases       The unit aliases
     */
    public Unit(String name, String plural, double unitInSeconds, String... aliases) {
        this.name = name;
        this.plural = plural;
        this.unitInSeconds = unitInSeconds;
        this.aliases.addAll(Arrays.asList(aliases));
    }

    /**
     * Gets the unit name.
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the unit name in plural.
     *
     * @return The unit name in plural
     */
    public String getPlural() {
        return plural;
    }

    /**
     * Gets the unit aliases.
     *
     * @return The aliases
     */
    public List<String> getAliases() {
        return aliases;
    }

    /**
     * Gets 1 of this unit in seconds
     *
     * @return The unit in seconds
     */
    public double getUnitInSeconds() {
        return unitInSeconds;
    }

    /**
     * Converts the value of a unit to another unit.
     *
     * @param value  The unit value
     * @param target The target unit
     * @return The value converted
     */
    public double convert(double value, Unit target) {
        return (value * unitInSeconds) / (target.getUnitInSeconds());
    }

    public Date addToDate(Date date, double value) {
        return new Date((long) (date.getTime() + (value * unitInSeconds * 1000)));
    }

    /**
     * Checks if a String is valid to an unit.
     *
     * @param string The string
     * @return If the String is the name or is in the aliases
     */
    public boolean isValid(String string) {
        return name.equalsIgnoreCase(string) || aliases.stream().anyMatch(alias -> alias.equalsIgnoreCase(string)) || plural.equalsIgnoreCase(string);
    }

}
