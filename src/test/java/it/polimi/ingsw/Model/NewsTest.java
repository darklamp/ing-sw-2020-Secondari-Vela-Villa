package it.polimi.ingsw.Model;

import it.polimi.ingsw.Network.SocketClientConnection;
import it.polimi.ingsw.Utility.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.net.Socket;

import static it.polimi.ingsw.Model.BuildingType.MIDDLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NewsTest {
    static Socket socket = new Socket();
    static Field a;
    static Field b;
    static GameTable gameTable = new GameTable(2);
    static SocketClientConnection c1 = new SocketClientConnection(socket, null);
    News news = new News("ASD", c1);
    static Cell cell1;
    static Cell cell2;
    static Player player1;
    Builder b1 = new Demeter(cell1, player1);
    Builder b2 = new Demeter(cell2, player1);

    @BeforeAll
    static void init() throws Exception {
        a = GameTable.class.getDeclaredField("news");
        a.setAccessible(true);
        b = GameTable.class.getDeclaredField("currentPlayer");
        b.setAccessible(true);
        cell1 = gameTable.getCell(1, 2);
        cell2 = gameTable.getCell(2, 2);
        player1 = new Player("gigi", gameTable, c1);
    }


    @Test
    void getCoords() {
        news.setCoords(1, 2, 0);
        Pair coord = news.getCoords();
        Assertions.assertEquals(coord, new Pair(1, 2));
    }

    @Test
    void setCoords() { //test first function setcoords
        news.setCoords(1,2,1);
        Assertions.assertEquals(news.getCoords(), new Pair(1,2));
        Assertions.assertEquals(news.getBuilder(gameTable), b1);
        Assertions.assertEquals(news.getHeight(), BuildingType.parse(0));
        //test second function setcoords
        news.setCoords(1,2,1,1);
        Assertions.assertEquals(news.getCoords(), new Pair(1,2));
        Assertions.assertEquals(news.getBuilder(gameTable), b1);
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
        Cell cell=news.getCell(gameTable);
        Assertions.assertEquals(cell,news.getCell(gameTable));
    }

    @Test
    void getBuilder() {
        news.setCoords(1, 2, 1);
        Assertions.assertEquals(news.getBuilder(gameTable),b1);
    }

    @Test
    void getHeight() {
        news.setCoords(1,2,1,1);
        Assertions.assertEquals(news.getHeight(),MIDDLE);
    }
}