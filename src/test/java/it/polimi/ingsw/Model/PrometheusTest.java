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

import static it.polimi.ingsw.Client.ClientState.*;

class PrometheusTest {

    @Test
    void isValidBuildTest() throws Exception {
        GameTable g = new GameTable(2);
        Player p1 = new Player("Giggino", g, "PROMETHEUS");
        Player p2 = new Player("Matte", g, "ATLAS");
        ArrayList<Player> players = new ArrayList<>();
        players.add(p2);
        players.add(p1);
        g.setPlayers(players);
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(3, 2);
        Cell c3 = g.getCell(4, 2);
        Cell c22 = g.getCell(0, 0);
        p1.initBuilderList(c3);
        p2.initBuilderList(c22);
        Builder b1 = p1.getBuilderList().get(0);
        Assertions.assertEquals(ClientState.MOVEORBUILD, b1.getFirstState());
        p1.setState(BUILD);
        p2.setState(BUILD);
        Assertions.assertEquals(WAIT, b1.isValidBuild(c1, BuildingType.BASE));
        p1.setState(MOVEORBUILD);
        Assertions.assertEquals(MOVE, b1.isValidBuild(c1, BuildingType.BASE));

        Assertions.assertThrows(InvalidBuildException.class, () -> c2.setHeight(b1, BuildingType.DOME));

    }

    @Test
    void isValidMoveTest() throws Exception {
        GameTable g = new GameTable(2);

        Player p1 = new Player("Giggino", g, "PROMETHEUS");
        Player p2 = new Player("Matte", g, "ATLAS");
        ArrayList<Player> players = new ArrayList<>();
        players.add(p2);
        players.add(p1);
        g.setPlayers(players);
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(3, 1);
        Cell c3 = g.getCell(3, 2);
        Cell c22 = g.getCell(0, 0);
        p1.initBuilderList(c1);
        p2.initBuilderList(c22);
        Builder b1 = p1.getBuilderList().get(0);
        p1.setState(MOVE);
        Assertions.assertThrows(InvalidMoveException.class, () -> b1.isValidMove(c2));
        p1.setState(MOVE);
        b1.setPosition(c3);
        b1.isValidMove(c2);
        p1.setState(ClientState.MOVE);
        TestUtilities.mustSetHeight(c1, BuildingType.BASE);
        TestUtilities.mustSetHeight(c3, BuildingType.NONE);
        Assertions.assertThrows(InvalidMoveException.class, () -> b1.isValidMove(c1));
        TestUtilities.mustSetHeight(c1, BuildingType.NONE);
        Assertions.assertDoesNotThrow(() -> b1.isValidMove(c1));
    }
}
