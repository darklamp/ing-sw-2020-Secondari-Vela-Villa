package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Controller.Exceptions.IllegalTurnStateException;
import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;
import it.polimi.ingsw.Network.SocketClientConnection;
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

class MainControllerTest {

    @Test
    void propertyChangeTest1() throws Exception {
        Socket socket = new Socket();

        Field a = GameTable.class.getDeclaredField("news");
        Field b = GameTable.class.getDeclaredField("type");
        a.setAccessible(true);
        b.setAccessible(true);
        GameTable gameTable = new GameTable(2);
        MainController controller = new MainController(gameTable);
        ArrayList<Integer> choices = new ArrayList<>();
        choices.add(1);
        choices.add(2);
        Field d = SocketClientConnection.class.getDeclaredField("out");
        d.setAccessible(true);
        SocketClientConnection c1 = new SocketClientConnection(socket, null);
        SocketClientConnection c2 = new SocketClientConnection(socket, null);
        d.set(c1, new ObjectOutputStream(OutputStream.nullOutputStream()));
        d.set(c2, new ObjectOutputStream(OutputStream.nullOutputStream()));

        Player player1 = new Player("gigi", gameTable, c1);
        Player player2 = new Player("gigi2", gameTable, c2);
        c1.setPlayer(player1);
        c2.setPlayer(player2);
        player1.setGod(choices.get(0));
        player2.setGod(choices.get(1));
        try {
            player1.initBuilderList(gameTable.getCell(2, 2));
            player1.initBuilderList(gameTable.getCell(2, 3));
            player2.initBuilderList(gameTable.getCell(1, 2));
            player2.initBuilderList(gameTable.getCell(1, 1));
        } catch (InvalidBuildException | InvalidCoordinateException e) {
            e.printStackTrace();
        }
        ArrayList<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        gameTable.setPlayers(players);
        gameTable.setGods(choices);
        News news = new News("ASD", c1);
        news.setInvalid();

        controller.propertyChange(new PropertyChangeEvent(new Object(), "ciao", null, news));
        News news2 = (News) a.get(gameTable);
        Assertions.assertFalse(news2.isValid());
        String stringa = (String) b.get(gameTable);
        Assertions.assertEquals(stringa, "INVALIDNEWS");
        News news4 = new News("asd", c2);
        controller.propertyChange(new PropertyChangeEvent(new Object(), "ABORT", null, news4));
        stringa = (String) b.get(gameTable);
        Assertions.assertEquals("ABORT", stringa);


    }

    @Test
    void propertyChangeTest2() throws Exception {
        Socket socket = new Socket();

        Field a = GameTable.class.getDeclaredField("news");
        Field b = GameTable.class.getDeclaredField("type");
        a.setAccessible(true);
        b.setAccessible(true);
        GameTable gameTable = new GameTable(2);
        MainController controller = new MainController(gameTable);
        News news = new News();
        news.setInvalid();
        ArrayList<Integer> choices = new ArrayList<>();
        choices.add(1);
        choices.add(2);
        Field d = SocketClientConnection.class.getDeclaredField("out");
        d.setAccessible(true);
        SocketClientConnection c1 = new SocketClientConnection(socket, null);
        SocketClientConnection c2 = new SocketClientConnection(socket, null);
        d.set(c1, new ObjectOutputStream(OutputStream.nullOutputStream()));
        d.set(c2, new ObjectOutputStream(OutputStream.nullOutputStream()));

        Player player1 = new Player("gigi", gameTable, c1);
        Player player2 = new Player("gigi2", gameTable, c2);
        c1.setPlayer(player1);
        c2.setPlayer(player2);
        player1.setGod(choices.get(0));
        player2.setGod(choices.get(1));
        try {
            player1.initBuilderList(gameTable.getCell(2, 2));
            player1.initBuilderList(gameTable.getCell(2, 3));
            player2.initBuilderList(gameTable.getCell(1, 2));
            player2.initBuilderList(gameTable.getCell(1, 1));
        } catch (InvalidBuildException | InvalidCoordinateException e) {
            e.printStackTrace();
        }
        ArrayList<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        gameTable.setPlayers(players);
        gameTable.setGods(choices);
        News news5 = new News("asd", c1);
        controller.propertyChange(new PropertyChangeEvent(new Object(), "MOVE", null, news5));
        String stringa = (String) b.get(gameTable);
        Assertions.assertEquals(stringa, "NOTYOURTURN");

        News news6 = new News("asd", c2);

        controller.propertyChange(new PropertyChangeEvent(new Object(), "PASS", null, news6));
        stringa = (String) b.get(gameTable);
        Assertions.assertEquals(stringa, "ILLEGALSTATE");

        controller.propertyChange(new PropertyChangeEvent(new Object(), "PLAYERTIMEOUT", null, news6));
        stringa = (String) b.get(gameTable);
        Assertions.assertEquals(stringa, "WIN");
    }

    @Test
    void propertyChangeTestWinner() throws Exception {
        Socket socket = new Socket();

        Field a = GameTable.class.getDeclaredField("news");
        Field b = GameTable.class.getDeclaredField("type");
        a.setAccessible(true);
        b.setAccessible(true);
        GameTable gameTable = new GameTable(2);
        MainController controller = new MainController(gameTable);
        News news = new News();
        news.setInvalid();
        ArrayList<Integer> choices = new ArrayList<>();
        choices.add(1);
        choices.add(2);
        Field d = SocketClientConnection.class.getDeclaredField("out");
        d.setAccessible(true);
        SocketClientConnection c1 = new SocketClientConnection(socket, null);
        SocketClientConnection c2 = new SocketClientConnection(socket, null);
        d.set(c1, new ObjectOutputStream(OutputStream.nullOutputStream()));
        d.set(c2, new ObjectOutputStream(OutputStream.nullOutputStream()));

        Player player1 = new Player("gigi", gameTable, c1);
        Player player2 = new Player("gigi2", gameTable, c2);
        c1.setPlayer(player1);
        c2.setPlayer(player2);
        player1.setGod(choices.get(0));
        player2.setGod(choices.get(1));
        try {
            player1.initBuilderList(gameTable.getCell(2, 2));
            player1.initBuilderList(gameTable.getCell(2, 3));
            player2.initBuilderList(gameTable.getCell(1, 2));
            player2.initBuilderList(gameTable.getCell(1, 1));
        } catch (InvalidBuildException | InvalidCoordinateException e) {
            e.printStackTrace();
        }
        ArrayList<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        gameTable.setPlayers(players);
        gameTable.setGods(choices);
        Method m = Cell.class.getDeclaredMethod("mustSetHeight", BuildingType.class);
        m.setAccessible(true);
        Cell c = gameTable.getCell(0, 0);
        Cell c4 = gameTable.getCell(1, 1);
        m.invoke(c, BuildingType.TOP);
        m.invoke(c4, BuildingType.MIDDLE);
        c.setBuilder(null);
        News news5 = new News("MOVE", c2);
        String stringa;
        news5.setCoords(0, 0, 1);
        controller.propertyChange(new PropertyChangeEvent(new Object(), "MOVE", null, news5));
        stringa = (String) b.get(gameTable);
        Assertions.assertEquals(stringa, "WIN");
    }

    @Test
    void isLegalStateTest() throws Exception {
        Method m = MainController.class.getDeclaredMethod("isLegalState", String.class, ClientState.class);
        m.setAccessible(true);
        String stringa = "ASD";
        AtomicInteger count = new AtomicInteger();
        for (ClientState t : ClientState.values()) {
            try {
                m.invoke(null, stringa, t);
            } catch (InvocationTargetException e) {
                if (e.getCause().getClass().equals(IllegalTurnStateException.class)) count.addAndGet(1);
            }
        }
        Assertions.assertEquals(6, count.get());
        count.set(0);
        for (ClientState t : ClientState.values()) {
            try {
                m.invoke(null, t.toString(), t);
            } catch (InvocationTargetException e) {
                if (e.getCause().getClass().equals(IllegalTurnStateException.class)) count.addAndGet(1);
            }
        }
        Assertions.assertEquals(4, count.get());
    }
}