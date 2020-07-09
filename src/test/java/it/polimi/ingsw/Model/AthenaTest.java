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
import it.polimi.ingsw.TestUtilities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static it.polimi.ingsw.Client.ClientState.MOVE;

public class AthenaTest {
    /**
     * This test first checks that the default gametable has athenaMove set to false; it then
     * proceeds to build on a cell and move on that cell with an Athena builder.
     * This is expected to trigger {@link GameTable#setAthenaMove(boolean)}, setting athenaMove to true.
     * The value is then set back to false with another call to the same method if, during the next turn, Athena
     * doesn't go up a level. This is also checked by this test.
     *
     * @throws Exception should not
     */
    @Test
    void isValidBuildTest() throws Exception {
        GameTable g = new GameTable(2);

        Player p1 = new Player("Giggino", g, "ATHENA");
        Player p2 = new Player("Giggino2", g, "ATLAS");
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        Cell c22 = g.getCell(0, 0);
        p1.initBuilderList(c1);
        p2.initBuilderList(c22);
        Builder b1 = p1.getBuilderList().get(0);

        Assertions.assertThrows(InvalidBuildException.class, () -> {
            b1.isValidBuild(c2, BuildingType.DOME);
        });
        b1.isValidBuild(c2, BuildingType.BASE);
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            b1.isValidBuild(c2, BuildingType.DOME);
        });
    }
    @Test
    void isValidMoveTest() throws Exception {
        GameTable g = new GameTable(2);
        Player p1 = new Player("Giggino", g, "ATHENA");
        Player p2 = new Player("Giggino2", g, "ATLAS");
        ArrayList<Player> players = new ArrayList<>();
        players.add(p2);
        players.add(p1);
        g.setPlayers(players);
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        Cell c3 = g.getCell(3, 4);
        Cell c22 = g.getCell(0, 0);
        p1.initBuilderList(c1);
        p2.initBuilderList(c22);
        Builder b1 = p1.getBuilderList().get(0);
        Field f = Athena.class.getDeclaredField("athenaMove");
        f.setAccessible(true);
        f.set(null, false);
        TestUtilities.mustSetHeight(c2, BuildingType.BASE);
        p1.setState(MOVE);
        b1.setPosition(c2);
        Assertions.assertTrue((Boolean) f.get(null));
        p1.setState(MOVE);
        b1.setPosition(c3);
        Assertions.assertFalse((Boolean) f.get(null));
    }

    @Test
    void moveHandicapTest() throws Exception {
        GameTable g = new GameTable(2);
        Player p1 = new Player("Giggino", g, "ATHENA");
        Player p2 = new Player("Giggino2", g, "ATLAS");
        ArrayList<Player> players = new ArrayList<>();
        players.add(p2);
        players.add(p1);
        g.setPlayers(players);
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        Cell c3 = g.getCell(3, 4);
        Cell c22 = g.getCell(0, 0);
        p1.initBuilderList(c1);
        p2.initBuilderList(c22);
        Builder b1 = p1.getBuilderList().get(0);
        Field f = Athena.class.getDeclaredField("athenaMove");
        f.setAccessible(true);
        f.set(null, false);
        Assertions.assertFalse(b1.moveHandicap(null, null));
        TestUtilities.mustSetHeight(c2, BuildingType.BASE);
        p1.setState(MOVE);
        b1.setPosition(c2);
        Assertions.assertTrue((Boolean) f.get(null));
        Assertions.assertFalse(b1.moveHandicap(c3, c2));
        Assertions.assertTrue(b1.moveHandicap(c2, c1));

        p1.setState(MOVE);
        b1.setPosition(c3);
        Assertions.assertFalse((Boolean) f.get(null));
    }
}
