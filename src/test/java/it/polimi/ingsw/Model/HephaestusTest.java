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

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;
import it.polimi.ingsw.TestUtilities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static it.polimi.ingsw.Model.BuildingType.BASE;

class HephaestusTest {

    @Test
    void isValidBuildTest1() throws Exception {
        GameTable g = new GameTable(2);

        Player p1 = new Player("Giggino", g, "HEPHAESTUS");
        Player p2 = new Player("Giggino2", g, "ATLAS");
        ArrayList<Player> players = new ArrayList<>();
        players.add(p2);
        players.add(p1);
        g.setPlayers(players);
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        Cell c22 = g.getCell(0, 0);

        p1.initBuilderList(c1);
        p2.initBuilderList(c22);

        Builder b1 = p1.getBuilderList().get(0);
        p1.setState(ClientState.BUILD);
        Assertions.assertDoesNotThrow(() -> c2.setHeight(b1, BASE));
        assert c2.getHeight() == BASE;
        p1.setFirstTime(true);
        TestUtilities.mustSetHeight(c2, BuildingType.TOP);
        Assertions.assertDoesNotThrow(() -> b1.isValidBuild(c2, BuildingType.DOME));
        p1.setFirstTime(true);
        TestUtilities.mustSetHeight(c2, BuildingType.MIDDLE);
        Assertions.assertDoesNotThrow(() -> b1.isValidBuild(c2, BuildingType.TOP));
        TestUtilities.mustSetHeight(c2, BASE);

        Assertions.assertThrows(InvalidBuildException.class, () -> c2.setHeight(b1, BuildingType.DOME));
        Assertions.assertThrows(InvalidBuildException.class, () -> c2.setHeight(b1, BuildingType.DOME));
    }

    @Test
    void isValidBuildTest2() throws Exception {
        GameTable g = new GameTable(2);
        Player p1 = new Player("Giggino", g, "HEPHAESTUS");
        Player p2 = new Player("Giggino2", g, "ATLAS");
        ArrayList<Player> players = new ArrayList<>();
        players.add(p2);
        players.add(p1);
        g.setPlayers(players);
        p1.setFirstTime(true);
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        Cell c3 = g.getCell(4, 2);
        Cell c4 = g.getCell(3, 3);
        Cell c22 = g.getCell(0, 0);
        p1.initBuilderList(c1);
        p2.initBuilderList(c22);
        Builder b1 = p1.getBuilderList().get(0);
        TestUtilities.mustSetHeight(c2, BuildingType.TOP);
        Assertions.assertDoesNotThrow(() -> b1.isValidBuild(c2, BuildingType.DOME));
        Field f = Hephaestus.class.getDeclaredField("previous");
        f.setAccessible(true);
        f.set(b1, c1);
        p1.setFirstTime(false);
        TestUtilities.mustSetHeight(c2, BuildingType.NONE);
        Assertions.assertThrows(InvalidBuildException.class, () -> c2.setHeight(b1, BASE));
        Assertions.assertThrows(InvalidBuildException.class, () -> c2.setHeight(b1, BuildingType.DOME));
        p1.setState(ClientState.BUILD);
        f.set(b1, c3);
        Assertions.assertThrows(InvalidBuildException.class, () -> c4.setHeight(b1, BuildingType.BASE));
    }

    @Test
    void isValidMoveTest() throws Exception {
        GameTable g = new GameTable(2);

        Player p1 = new Player("Giggino", g, "HEPHAESTUS");
        Player p2 = new Player("Giggino2", g, "ATLAS");

        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(3, 1);
        Cell c3 = g.getCell(3, 2);
        Cell c4 = g.getCell(4, 2);
        Cell c22 = g.getCell(0, 0);

        p1.initBuilderList(c1);
        p2.initBuilderList(c22);

        Builder b1 = p1.getBuilderList().get(0);
        Assertions.assertThrows(InvalidMoveException.class, () -> b1.isValidMove(c2));
        b1.isValidMove(c3);
        b1.isValidMove(c4);
    }
}

