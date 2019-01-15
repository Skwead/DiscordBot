package br.com.finalelite.bots.supporter.utils.time.unit;

/**
 * A RSpigot class.
 * Part of the time unit API. Used to mark a unit as a long (day/week/month etc) unit.
 *
 * @author Paulo
 * @version 1.0
 */
public class LongUnit extends Unit {
    /**
     * Builds a new long time unit.
     *
     * @param name          The unit name
     * @param plural        The unit name in plural
     * @param unitInSeconds 1 of this unit in seconds
     * @param aliases       The unit aliases
     */
    public LongUnit(String name, String plural, int unitInSeconds, String... aliases) {
        super(name, plural, unitInSeconds, aliases);
    }
}
