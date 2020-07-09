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

import java.util.ArrayList;

import static it.polimi.ingsw.Client.ClientState.BUILD;
import static it.polimi.ingsw.Client.ClientState.WAIT;
import static it.polimi.ingsw.Model.BuildingType.DOME;

class AtlasTest {

    /**
     * tests that the the atlas logic works by building a dome on an empty cell and then verifies that I can't build on the now occupied cell anymore
     *
     * @throws Exception --> there's something wrong (that is, if the test does fine no exception are thrown)
     */
    @Test
    void isValidBuildTest() throws Exception {
        GameTable g = new GameTable(2);
        Player p1 = new Player("Giggino", g, "ATLAS");
        Player p2 = new Player("Giggino2", g, "ATLAS");
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        p1.initBuilderList(c1);
        p1.initBuilderList(g.getCell(2, 3));
        p2.initBuilderList(g.getCell(1, 2));
        p2.initBuilderList(g.getCell(3, 4));
        ArrayList<Player> players = new ArrayList<>();
        players.add(p2);
        players.add(p1);
        g.setPlayers(players);
        Builder b1 = p1.getBuilderList().get(0);
        p1.setState(ClientState.BUILD);
        ClientState a = b1.isValidBuild(c2, DOME);
        Assertions.assertEquals(WAIT, a);
        Assertions.assertDoesNotThrow(() -> c2.setHeight(b1, DOME));
        assert c2.getHeight() == DOME;
        Assertions.assertThrows(InvalidBuildException.class, () -> b1.isValidBuild(c2, DOME));
        Cell c3 = g.getCell(3, 3);
        p2.setState(BUILD);
        Assertions.assertDoesNotThrow(() -> c3.setHeight(p2.getBuilderList().get(1), DOME));
        assert c2.getHeight() == DOME;

        TestUtilities.mustSetHeight(c3, DOME);
        Assertions.assertThrows(InvalidBuildException.class, () -> b1.isValidBuild(c3, DOME));
        Cell c4 = g.getCell(4, 2);
        Assertions.assertDoesNotThrow(() -> b1.isValidBuild(c4, BuildingType.BASE));
        TestUtilities.mustSetHeight(c3, BuildingType.BASE);
        Assertions.assertDoesNotThrow(() -> b1.isValidBuild(c3, BuildingType.MIDDLE));
    }

    @Test
    void isValidMoveTest() throws Exception {
        GameTable g = new GameTable(2);

        Player p1 = new Player("Giggino", g, "ATLAS");
        Player p2 = new Player("Giggino2", g, "APOLLO");

        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(3, 1);
        Cell c3 = g.getCell(3, 2);
        Cell c4 = g.getCell(4, 2);
        Cell c22 = g.getCell(0, 0);
        p1.initBuilderList(c1);
        p2.initBuilderList(c22);
        Builder b1 = p1.getBuilderList().get(0);

        Assertions.assertThrows(InvalidMoveException.class, () -> b1.isValidMove(c2));
        p1.setState(ClientState.MOVE);
        b1.isValidMove(c3);
        b1.isValidMove(c4);
    }

}