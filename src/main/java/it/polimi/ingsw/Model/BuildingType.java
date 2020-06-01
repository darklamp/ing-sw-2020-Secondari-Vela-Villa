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
    public BuildingType getNext() throws InvalidBuildException {
        switch (this) {
            case NONE -> {
                return BASE;
            }
            case BASE -> {
                return MIDDLE;
            }
            case MIDDLE -> {
                return TOP;
            }
            case TOP -> {
                return DOME;
            }
            default -> throw new InvalidBuildException();
        }
    }

    /**
     * "Parses" int to return correspondent BuildingType
     *
     * @param i int to be parsed.
     * @return BuildingType associated with the given int.
     */
    public static BuildingType parse(int i) {
        switch (i) {
            case 1 -> {
                return MIDDLE;
            }
            case 2 -> {
                return TOP;
            }
            case 3 -> {
                return DOME;
            }
            default -> {
                return BASE;
            }
        }
    }
}
