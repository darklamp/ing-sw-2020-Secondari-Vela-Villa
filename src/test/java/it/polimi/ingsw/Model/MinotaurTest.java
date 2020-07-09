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
import it.polimi.ingsw.Utility.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;

class MinotaurTest {

    @Test
    void isValidBuildTest() throws Exception {
        GameTable g = new GameTable(2);

        Player p1 = new Player("Giggino", g, "MINOTAUR");
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
        Player p1 = new Player("Giggino", g, "MINOTAUR");
        Player p2 = new Player("Giggino2", g, "ATLAS");

        ArrayList<Player> players = new ArrayList<>();
        players.add(p2);
        players.add(p1);
        g.setPlayers(players);
        Cell c0 = g.getCell(4, 2);
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        Cell c3 = g.getCell(4, 1);
        Cell c4 = g.getCell(3, 3);
        p1.initBuilderList(c0);
        p2.initBuilderList(c1);
        p1.initBuilderList(c4);
        Builder b1 = p1.getBuilderList().get(0);

        Assertions.assertDoesNotThrow(() -> b1.isValidMove(c1));
        Assertions.assertThrows(InvalidMoveException.class, () -> b1.isValidMove(c2));
        Assertions.assertThrows(InvalidMoveException.class, () -> b1.isValidMove(c4));
        p1.setState(ClientState.MOVE);
        Assertions.assertDoesNotThrow(() -> b1.setPosition(c3));

    }

    @Test
    void checkEmptyCellBehindTest() throws Exception {
        GameTable g = new GameTable(2);
        Player p1 = new Player("Giggino", g, "MINOTAUR");
        Player p2 = new Player("Giggino2", g, "ATLAS");
        Cell c0 = g.getCell(4, 2);
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        Cell c3 = g.getCell(3, 2);
        Cell c4 = g.getCell(2, 2);
        Cell c5 = g.getCell(3, 1);
        Cell c6 = g.getCell(2, 0);
        Builder b1 = new Minotaur(c0, p1);
        Builder b2 = new Atlas(c2, p2);
        Assertions.assertFalse(((Minotaur) b1).checkEmptyCellBehind(c1));
        Assertions.assertTrue(((Minotaur) b1).checkEmptyCellBehind(c3));
        Assertions.assertTrue(((Minotaur) b1).checkEmptyCellBehind(c5));
        Builder b3 = new Atlas(c6, p2);
        Assertions.assertFalse(((Minotaur) b1).checkEmptyCellBehind(c5));


    }

    @Test
    void checkEmptyCellBehindTest2() throws Exception {
        GameTable g = new GameTable(2);
        Player p1 = new Player("Giggino", g, "MINOTAUR");
        Player p2 = new Player("Giggino2", g, "ATLAS");

        Cell c2 = g.getCell(4, 4);
        Cell c7 = g.getCell(1, 1);
        Cell c8 = g.getCell(1, 2);

        p2.initBuilderList(c8);
        Field f = Cell.class.getDeclaredField("coordinates");
        f.setAccessible(true);
        f.set(c7, new Pair(5, 5));
        c7.setBuilder(p2.getBuilderList().get(0));

        Builder b1 = new Minotaur(c2, p1);
        Assertions.assertThrows(InvalidMoveException.class, () -> ((Minotaur) b1).checkEmptyCellBehind(c7));
        Assertions.assertThrows(AssertionError.class, () -> ((Minotaur) b1).executeMove(c7));


    }

    @Test
    void getCellBehindTest() throws Exception {
        GameTable g = new GameTable(2);
        Player p1 = new Player("Giggino", g, "MINOTAUR");
        Player p2 = new Player("Giggino2", g, "MINOTAUR");
        Cell c = g.getCell(3, 3);
        Cell c0 = g.getCell(4, 2);
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(1, 1);
        Cell c3 = g.getCell(3, 2);
        Cell c4 = g.getCell(2, 2);
        Cell c5 = g.getCell(3, 1);
        Cell c6 = g.getCell(1, 2);
        Cell c7 = g.getCell(2, 1);
        Cell c8 = g.getCell(3, 3);
        Cell c9 = g.getCell(2, 3);
        Cell c10 = g.getCell(1, 3);
        Cell c11 = g.getCell(4, 4);
        Cell c12 = g.getCell(4, 1);

        Minotaur b1 = new Minotaur(c0, p1);
        Minotaur b2 = new Minotaur(c4, p2);
        Minotaur b3 = new Minotaur(c, p2);
        Assertions.assertEquals(b1.getCellBehind(c1).getFirst(), 4);
        Assertions.assertEquals(b1.getCellBehind(c1).getSecond(), 4);
        Assertions.assertEquals(b1.getCellBehind(c3).getFirst(), 2);
        Assertions.assertEquals(b1.getCellBehind(c3).getSecond(), 2);
        Assertions.assertEquals(b1.getCellBehind(c5).getFirst(), 2);
        Assertions.assertEquals(b1.getCellBehind(c5).getSecond(), 0);
        Assertions.assertEquals(b2.getCellBehind(c5).getFirst(), 4);
        Assertions.assertEquals(b2.getCellBehind(c5).getSecond(), 0);


        Assertions.assertFalse(b2.checkEmptyCellBehind(c3));
        Assertions.assertTrue(b2.checkEmptyCellBehind(c6));
        Assertions.assertEquals(b2.getCellBehind(c6), g.getCell(0, 2).getPosition());
        Assertions.assertTrue(b2.checkEmptyCellBehind(c7));
        Assertions.assertEquals(b2.getCellBehind(c7), g.getCell(2, 0).getPosition());
        Assertions.assertTrue(b2.checkEmptyCellBehind(c2));
        Assertions.assertEquals(b2.getCellBehind(c2), g.getCell(0, 0).getPosition());
        Assertions.assertEquals(b2.getCellBehind(c11), g.getCell(0, 0).getPosition());
        Assertions.assertFalse(b2.checkEmptyCellBehind(c11));
        Assertions.assertTrue(b2.checkEmptyCellBehind(c5));
        Assertions.assertEquals(b2.getCellBehind(c9), g.getCell(2, 4).getPosition());
        Assertions.assertEquals(b2.getCellBehind(c3), g.getCell(4, 2).getPosition());
        Assertions.assertEquals(b2.getCellBehind(c8), c11.getPosition());
        Assertions.assertEquals(b2.getCellBehind(c2), g.getCell(0, 0).getPosition());
        Assertions.assertEquals(b2.getCellBehind(c10), g.getCell(0, 4).getPosition());
        Assertions.assertTrue(b2.checkEmptyCellBehind(c8));
        Assertions.assertTrue(b2.checkEmptyCellBehind(c9));
        Assertions.assertTrue(b2.checkEmptyCellBehind(c10));

        Assertions.assertFalse(b3.checkEmptyCellBehind(c11));
        Minotaur b4 = new Minotaur(c12, p2);
        Assertions.assertFalse(b4.checkEmptyCellBehind(g.getCell(4, 0)));
        Cell c13 = g.getCell(3, 4);
        Minotaur b5 = new Minotaur(c13, p2);
        Assertions.assertFalse(b5.checkEmptyCellBehind(g.getCell(4, 4)));
        Cell c14 = g.getCell(1, 1);
        Minotaur b6 = new Minotaur(c14, p2);
        Assertions.assertFalse(b6.checkEmptyCellBehind(g.getCell(0, 0)));
        Assertions.assertFalse(b6.checkEmptyCellBehind(g.getCell(0, 1)));
        Assertions.assertFalse(b6.checkEmptyCellBehind(g.getCell(0, 2)));
        Cell c15 = g.getCell(0, 1);
        Minotaur b7 = new Minotaur(c15, p2);
        Assertions.assertFalse(b7.checkEmptyCellBehind(g.getCell(0, 0)));
        Cell c16 = g.getCell(0, 3);
        Minotaur b8 = new Minotaur(c16, p2);
        Assertions.assertFalse(b8.checkEmptyCellBehind(g.getCell(0, 4)));


    }
}