package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Controller.Exceptions.IllegalTurnStateException;
import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;
import it.polimi.ingsw.Network.GameInitializer;
import it.polimi.ingsw.Network.Server;
import it.polimi.ingsw.Network.SocketClientConnection;
import it.polimi.ingsw.ServerMain;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeEvent;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
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
        Assertions.assertEquals("PLAYERKICKED", stringa);
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
        Assertions.assertEquals(player1.getState(), ClientState.WIN);
        Assertions.assertEquals(stringa, "PLAYERKICKED");
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
        Assertions.assertEquals(player2.getState(), ClientState.WIN);
        Assertions.assertEquals("PLAYERKICKED", stringa);

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

    @Test
    void testNews() throws Exception {
        MainController mainController = new MainController(new GameTable(2));
        Method m = MainController.class.getDeclaredMethod("getNews");
        m.setAccessible(true);
        Field f = MainController.class.getDeclaredField("news");
        f.setAccessible(true);
        Assertions.assertEquals(f.get(mainController), m.invoke(mainController));
    }

    @Test
    void constructorTest() throws Exception {
        Field per = ServerMain.class.getDeclaredField("persistence");
        per.setAccessible(true);
        per.set(null, true);
        Field file = MainController.class.getDeclaredField("fileOutputStream");
        file.setAccessible(true);
        MainController mainController = new MainController(new GameTable(2));
        Assertions.assertNotNull(file.get(mainController));
    }

    @Test
    void testGameInitializer() throws Exception {
        Field file = MainController.class.getDeclaredField("gameInitializer");
        file.setAccessible(true);
        MainController mainController = new MainController(new GameTable(2));
        Assertions.assertNull(file.get(mainController));
        mainController.setGameInitializer(new GameInitializer(null, null, null, null, null, null, null, null, null));
        Assertions.assertNotNull(file.get(mainController));
    }

    @Test
    void consoleKickPlayerTest() throws Exception {
        GameTable g = new GameTable(3);
        ArrayList<Player> p = new ArrayList<>();
        p.add(new Player("gigi2", g, null));
        p.add(new Player("gigi3", g, null));
        Player p1 = new Player("gigi", g, new SocketClientConnection(new Socket(), null));
        p1.setGod(0);

        p1.initBuilderList(g.getCell(0, 0));
        p1.initBuilderList(g.getCell(0, 1));
        p.add(p1);

        g.setPlayers(p);
        MainController mainController = new MainController(g);
        mainController.consoleKickPlayer("gigi");
        Assertions.assertEquals(2, g.getPlayers().size());
    }

    @Test
    void containsPlayerTest() throws Exception {
        GameTable g = new GameTable(3);
        ArrayList<Player> p = new ArrayList<>();
        p.add(new Player("gigi2", g, null));
        p.add(new Player("gigi3", g, null));
        Player p1 = new Player("gigi", g, new SocketClientConnection(new Socket(), null));
        p1.setGod(0);

        p1.initBuilderList(g.getCell(0, 0));
        p1.initBuilderList(g.getCell(0, 1));
        p.add(p1);

        g.setPlayers(p);
        MainController mainController = new MainController(g);

        Assertions.assertEquals(2, mainController.containsPlayer("gigi"));
        Assertions.assertEquals(-1, mainController.containsPlayer("gigi4"));

    }

    @Test
    void setPlayerFromDiskTest() throws Exception {
        GameTable g = new GameTable(3);
        ArrayList<Player> p = new ArrayList<>();
        p.add(new Player("gigi2", g, null));
        p.add(new Player("gigi3", g, null));
        Player p1 = new Player("gigi", g, new SocketClientConnection(new Socket(), null));
        p1.setGod(0);

        p1.initBuilderList(g.getCell(0, 0));
        p1.initBuilderList(g.getCell(0, 1));
        p.add(p1);

        g.setPlayers(p);
        MainController mainController = new MainController(g);
        SocketClientConnection c3 = new SocketClientConnection(null, null);
        mainController.setPlayerFromDisk("gigi", c3);
        Field f1 = Player.class.getDeclaredField("connection");
        f1.setAccessible(true);
        Assertions.assertEquals(f1.get(p1), c3);
    }

    @Test
    void restartFromDiskTest() throws Exception {
        GameTable g = new GameTable(3);
        ArrayList<Player> p = new ArrayList<>();
        p.add(new Player("gigi2", g, null));
        p.add(new Player("gigi3", g, null));
        Player p1 = new Player("gigi", g, null);
        p1.setGod(0);

        p1.initBuilderList(g.getCell(0, 0));
        p1.initBuilderList(g.getCell(0, 1));
        p.add(p1);

        g.setPlayers(p);
        MainController mainController = new MainController(g);
        Socket socket = new Socket();
        Server s = new Server();
        Field f3 = Server.class.getDeclaredField("serverSocket");
        f3.setAccessible(true);
        Field f4 = ServerMain.class.getDeclaredField("persistence");
        f4.setAccessible(true);
        f4.set(null, false);
        f3.set(s, new ServerSocket(1337, 1, InetAddress.getByName("localhost")));
        new Thread(s::run).start();
        Socket socket1 = new Socket("localhost", 1337);
        Socket socket2 = new Socket("localhost", 1337);
        Socket socket3 = new Socket("localhost", 1337);

        SocketClientConnection c3 = new SocketClientConnection(socket1, s);
        SocketClientConnection c2 = new SocketClientConnection(socket2, s);
        SocketClientConnection c1 = new SocketClientConnection(socket3, s);
        Field f2 = SocketClientConnection.class.getDeclaredField("out");
        f2.setAccessible(true);
        f2.set(c1, new ObjectOutputStream(socket1.getOutputStream()));
        f2.set(c2, new ObjectOutputStream(socket2.getOutputStream()));
        f2.set(c3, new ObjectOutputStream(socket3.getOutputStream()));

        ArrayList<SocketClientConnection> list = new ArrayList<>();
        list.add(c1);
        list.add(c2);
        list.add(c3);
        mainController.restartFromDisk(list);
        Field f = SocketClientConnection.class.getDeclaredField("ready");
        f.setAccessible(true);
        Assertions.assertTrue((boolean) f.get(c1));
        Assertions.assertTrue((boolean) f.get(c2));
        Assertions.assertTrue((boolean) f.get(c3));

    }
}