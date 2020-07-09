/*
 * Santorini
 * Copyright (C)  2020  Alessandro Villa and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Affero General Public License as
 *      published by the Free Software Foundation, either version 3 of the
 *      License, or (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Affero General Public License for more details.
 *
 *      You should have received a copy of the GNU Affero General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * E-mail contact addresses:
 * darklampz@gmail.com
 * alessandro17.villa@mail.polimi.it
 *
 */

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
