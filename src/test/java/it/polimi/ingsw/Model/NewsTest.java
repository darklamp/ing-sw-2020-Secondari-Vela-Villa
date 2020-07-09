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

import it.polimi.ingsw.Network.SocketClientConnection;
import it.polimi.ingsw.Utility.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.net.Socket;
import java.util.ArrayList;

import static it.polimi.ingsw.Model.BuildingType.MIDDLE;

class NewsTest {
    static Socket socket = new Socket();
    static Field a;
    static Field b;
    static GameTable gameTable = new GameTable(2);
    static SocketClientConnection c1 = new SocketClientConnection(socket, null);
    static SocketClientConnection c2 = new SocketClientConnection(socket, null);
    News news = new News("ASD", c1);
    static Cell cell1;
    static Cell cell2;
    static Player player1;
    static Player player2;

    @BeforeAll
    static void init() throws Exception {
        a = GameTable.class.getDeclaredField("news");
        a.setAccessible(true);
        b = GameTable.class.getDeclaredField("currentPlayer");
        b.setAccessible(true);
        cell1 = gameTable.getCell(1, 2);
        cell2 = gameTable.getCell(2, 2);
        player1 = new Player("gigi", gameTable, c1);
        player2 = new Player("gigi2", gameTable, c2);
        player1.setGod(0);
        player2.setGod(1);
        player1.initBuilderList(cell1);
        player1.initBuilderList(cell2);
        player2.initBuilderList(gameTable.getCell(3, 3));
        player2.initBuilderList(gameTable.getCell(3, 4));

    }


    @Test
    void getCoords() {
        news.setCoords(1, 2, 0);
        Pair coord = news.getCoords();
        Assertions.assertEquals(coord, new Pair(1, 2));
    }

    @Test
    void setCoords() { //test first function setcoords
        news.setCoords(1, 2, 0, 0);
        Assertions.assertEquals(news.getCoords(), new Pair(1, 2));
        Assertions.assertEquals(news.getBuilder(gameTable), player2.getBuilderList().get(0));
        Assertions.assertEquals(news.getHeight(), BuildingType.parse(0));
        //test second function setcoords
        news.setCoords(1,2,0,1);
        Assertions.assertEquals(news.getCoords(), new Pair(1, 2));
        Assertions.assertEquals(news.getBuilder(gameTable), player2.getBuilderList().get(0));
        Assertions.assertEquals(news.getHeight(), BuildingType.parse(1));

    }

    @Test
    void getSender() {
        SocketClientConnection c2 =news.getSender();
        Assertions.assertEquals(c2, c1);


    }


    @Test
    void isValid() {
        Boolean bool= news.isValid();
        Assertions.assertEquals(bool, news.isValid());

    }

    @Test
    void setInvalid() {
        news.setInvalid();
        Assertions.assertEquals(false, news.isValid());
    }

    @Test
    void getString() {
        Assertions.assertEquals(news.getString(),"ASD");
    }

    @Test
    void getCell() {
        news.setCoords(1, 2, 0);
        Cell cell = news.getCell(gameTable);
        Assertions.assertEquals(cell, news.getCell(gameTable));
        news.setCoords(5, 2, 0);
        Assertions.assertNull(news.getCell(gameTable));
    }

    @Test
    void getBuilder() {
        news.setCoords(1, 2, 0);
        Assertions.assertEquals(news.getBuilder(gameTable), player2.getBuilderList().get(0));
    }

    @Test
    void getHeight() {
        news.setCoords(1, 2, 1, 1);
        Assertions.assertEquals(news.getHeight(), MIDDLE);
    }

    @Test
    void getRecipientsTest() {
        news.setRecipients((ArrayList<SocketClientConnection>) null);
        Assertions.assertNull(news.getRecipients());
        news.setRecipients(player1);
        Assertions.assertEquals(player1.getConnection(), news.getRecipients().get(0));
    }
}