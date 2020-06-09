package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;

/**
 * Enum which holds building types.
 */
public enum BuildingType {
    NONE, BASE, MIDDLE, TOP, DOME;

    /**
     * @return next building level if not dome, otherwise throws.
     * @throws InvalidBuildException in case a DOME gets passed.
     */
    public BuildingType getNext() throws IllegalArgumentException {
        return switch (this) {
            case NONE -> BASE;
            case BASE -> MIDDLE;
            case MIDDLE -> TOP;
            case TOP -> DOME;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * "Parses" int to return correspondent BuildingType
     *
     * @param i int to be parsed.
     * @return BuildingType associated with the given int.
     */
    public static BuildingType parse(int i) {
        return switch (i) {
            case 1 -> MIDDLE;
            case 2 -> TOP;
            case 3 -> DOME;
            default -> BASE;
        };
    }
}
