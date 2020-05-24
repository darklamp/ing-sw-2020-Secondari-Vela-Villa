package it.polimi.ingsw.Model;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Controller.Exceptions.IllegalTurnStateException;
import it.polimi.ingsw.Controller.MainController;
import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;
import it.polimi.ingsw.Model.Exceptions.NickAlreadyTakenException;
import it.polimi.ingsw.Network.SocketClientConnection;
import it.polimi.ingsw.Utility.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeEvent;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class NewsTest {
    Socket socket = new Socket();
    Field a = GameTable.class.getDeclaredField("news");
    a.setAccessible(true);
    GameTable gameTable = new GameTable(2);
    MainController controller = new MainController(gameTable);
    SocketClientConnection c1 = new SocketClientConnection(socket, null);
    News news = new News("ASD", c1);
    Cell cell1 = gameTable.getCell(1,2);
    Player player1 = new Player("gigi", gameTable, c1);
    Builder b1 = new Demeter(cell1,player1);

    NewsTest() throws NoSuchFieldException, NickAlreadyTakenException {
    }


    @Test
    void getCoords() {
        news.setCoords(1,2,0);
        Pair coord= news.getCoords();
        Assertions.assertEquals(coord, new Pair(1,2));
    }

    @Test
    void setCoords() { //test first function setcoords
        news.setCoords(1,2,0);
        Assertions.assertEquals(news.getCoords(), new Pair(1,2));
        Assertions.assertEquals(news.getBuilder(gameTable), 0);
        Assertions.assertEquals(news.getHeight(), null);
        //test second function setcoords
        news.setCoords(1,2,1,1);
        Assertions.assertEquals(news.getCoords(), new Pair(1,2));
        Assertions.assertEquals(news.getBuilder(gameTable), 0);
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
        Cell cell=news.getCell(gameTable);
        Assertions.assertEquals(cell,news.getCell(gameTable));
    }

    @Test
    void getBuilder() {
        Assertions.assertEquals(news.getBuilder(gameTable),b1);
    }

    @Test
    void getHeight() {
        news.setCoords(1,2,1,1);
        Assertions.assertEquals(news.getHeight(),1);
    }
}