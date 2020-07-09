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
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static it.polimi.ingsw.Client.ClientState.MOVE;

class ApolloTest {

    @Test
    void isValidBuildTest() throws Exception {
        GameTable g = new GameTable(2);

        Player p1 = new Player("Giggino", g, "APOLLO");
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
    void executeMoveTest() throws Exception {
        GameTable g = new GameTable(2);
        Player p1 = new Player("Giggino", g, "APOLLO");
        Player p2 = new Player("Pippo", g, "ATLAS");
        ArrayList<Player> players = new ArrayList<>();
        players.add(p2);
        players.add(p1);
        g.setPlayers(players);
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        Cell c3 = g.getCell(4, 0);
        Cell c4 = g.getCell(4, 2);
        p1.initBuilderList(c1);
        p2.initBuilderList(c2);
        p1.initBuilderList(c4);
        Builder b1 = p1.getBuilderList().get(0);
        Builder b2 = p2.getBuilderList().get(0);
        Builder b3 = p1.getBuilderList().get(1);
        Assertions.assertDoesNotThrow(() -> b1.executeMove(c2));
        assert c2.getBuilder() == b1 && c1.getBuilder() == b2;
    }

    @Test
    void isValidMoveTest() throws Exception {
        GameTable g = new GameTable(2);
        Player p1 = new Player("Giggino", g, "APOLLO");
        Player p2 = new Player("Pippo", g, "ATLAS");
        ArrayList<Player> players = new ArrayList<>();
        players.add(p2);
        players.add(p1);
        g.setPlayers(players);
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        Cell c3 = g.getCell(4, 0);
        Cell c4 = g.getCell(4, 2);
        p1.initBuilderList(c1);
        p2.initBuilderList(c2);
        p1.initBuilderList(c4);
        Builder b1 = p1.getBuilderList().get(0);
        Builder b2 = p2.getBuilderList().get(0);
        Builder b3 = p1.getBuilderList().get(1);
        Assertions.assertDoesNotThrow(() -> b1.isValidMove(c2));
        Assertions.assertThrows(InvalidMoveException.class, () -> b1.isValidMove(c4));
        Assertions.assertThrows(InvalidMoveException.class, () -> b1.setPosition(c1));
        Assertions.assertThrows(InvalidMoveException.class, () -> b1.setPosition(c3));
        Cell c0 = g.getCell(3, 3);
        p1.setState(MOVE);
        b1.setPosition(c0); // shouldn't throw
    }
}