package br.com.finalelite.bots.supporter.utils.time.unit;

/**
 * A RSpigot class.
 * Part of the time unit API. Used to mark a unit as a short (seconds/minutes/hours etc) unit.
 *
 * @author Paulo
 * @version 1.0
 */
public class ShortUnit extends Unit {
    /**
     * Builds a new short time unit.
     *
     * @param name          The unit name
     * @param plural        The unit name in plural
     * @param unitInSeconds 1 of this unit in seconds
     * @param aliases       The unit aliases
     */
    public ShortUnit(String name, String plural, int unitInSeconds, String... aliases) {
        super(name, plural, unitInSeconds, aliases);
    }
}
