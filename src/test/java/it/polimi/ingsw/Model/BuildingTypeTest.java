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

import org.junit.jupiter.api.Test;

import static it.polimi.ingsw.Model.BuildingType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BuildingTypeTest {

    @Test
    void getNextTest() throws Exception {
        BuildingType b = NONE;
        assertEquals(b.getNext(), BASE);
        b = BASE;
        assertEquals(b.getNext(), MIDDLE);
        b = MIDDLE;
        assertEquals(b.getNext(), TOP);
        b = TOP;
        assertEquals(b.getNext(), DOME);
        b = DOME;

        BuildingType finalB = b;
        assertThrows(IllegalArgumentException.class, finalB::getNext);
    }

    @Test
    void parse() {
        int i = 1;
        assertEquals(BuildingType.parse(i), MIDDLE);
        i = 2;
        assertEquals(BuildingType.parse(i), TOP);
        i = 3;
        assertEquals(BuildingType.parse(i), DOME);
        i = -1;
        assertEquals(BuildingType.parse(i), BASE);

    }
}