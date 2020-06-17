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
import it.polimi.ingsw.TestUtilities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeEvent;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static it.polimi.ingsw.Client.ClientState.*;

class MainControllerTest {

    /**
     * Tests {@link MainController#propertyChange(PropertyChangeEvent)} in the case where there's an invalid
     * or ABORT news
     *
     * @throws Exception if something fails
     */
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
        Assertions.assertEquals("ABORT", stringa);
    }

    /**
     * Tests {@link MainController#propertyChange(PropertyChangeEvent)} in a PLAYERTIMEOUT case and in
     * a couple cases where the player is trying to do something it's not supposed to
     *
     * @throws Exception if something fails
     */
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
        Assertions.assertEquals(stringa, "WIN");
    }

    /**
     * Tests {@link MainController#propertyChange(PropertyChangeEvent)} in the case where there's a winner
     *
     * @throws Exception if something fails
     */
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
        Cell c = gameTable.getCell(0, 0);
        Cell c4 = gameTable.getCell(1, 1);
        TestUtilities.mustSetHeight(c, BuildingType.TOP);
        TestUtilities.mustSetHeight(c4, BuildingType.MIDDLE);
        c.setBuilder(null);
        News news5 = new News("MOVE", c2);
        String stringa;
        news5.setCoords(0, 0, 1);
        controller.propertyChange(new PropertyChangeEvent(new Object(), "MOVE", null, news5));
        stringa = (String) b.get(gameTable);
        Assertions.assertEquals(player2.getState(), ClientState.WIN);
        Assertions.assertEquals("WIN", stringa);

    }

    /**
     * Tests {@link MainController#propertyChange(PropertyChangeEvent)} in the case where
     * there are 3 players, 2 of which have no more valid moves
     *
     * @throws Exception if something fails
     */
    @Test
    void propertyChangeTestWinner2() throws Exception {
        Socket socket = new Socket();

        Field a = GameTable.class.getDeclaredField("news");
        Field b = GameTable.class.getDeclaredField("type");
        a.setAccessible(true);
        b.setAccessible(true);
        GameTable gameTable = new GameTable(3);
        MainController controller = new MainController(gameTable);
        News news = new News();
        news.setInvalid();
        ArrayList<Integer> choices = new ArrayList<>();
        choices.add(1);
        choices.add(2);
        choices.add(3);
        Field d = SocketClientConnection.class.getDeclaredField("out");
        d.setAccessible(true);
        SocketClientConnection c1 = new SocketClientConnection(socket, null);
        SocketClientConnection c2 = new SocketClientConnection(socket, null);
        SocketClientConnection c3 = new SocketClientConnection(socket, null);

        d.set(c1, new ObjectOutputStream(OutputStream.nullOutputStream()));
        d.set(c2, new ObjectOutputStream(OutputStream.nullOutputStream()));
        d.set(c3, new ObjectOutputStream(OutputStream.nullOutputStream()));


        Player player1 = new Player("gigi", gameTable, c1);
        Player player2 = new Player("gigi2", gameTable, c2);
        Player player3 = new Player("gigi3", gameTable, c3);

        c1.setPlayer(player1);
        c2.setPlayer(player2);
        c2.setPlayer(player3);
        player1.setGod(choices.get(0));
        player2.setGod(choices.get(1));
        player3.setGod(choices.get(2));

        try {
            player1.initBuilderList(gameTable.getCell(2, 2));
            player1.initBuilderList(gameTable.getCell(2, 3));
            player2.initBuilderList(gameTable.getCell(0, 0));
            player2.initBuilderList(gameTable.getCell(0, 1));
            player3.initBuilderList(gameTable.getCell(4, 3));
            player3.initBuilderList(gameTable.getCell(4, 4));
        } catch (InvalidBuildException | InvalidCoordinateException e) {
            e.printStackTrace();
        }
        ArrayList<Player> players = new ArrayList<>();
        players.add(player3);
        players.add(player1);
        players.add(player2);
        gameTable.setPlayers(players);
        Cell c = gameTable.getCell(1, 1);
        Cell c4 = gameTable.getCell(1, 0);
        Cell c8 = gameTable.getCell(1, 2);
        Cell c9 = gameTable.getCell(0, 2);
        c3.setPlayer(player3);
        TestUtilities.mustSetHeight(c8, BuildingType.DOME);
        TestUtilities.mustSetHeight(c, BuildingType.DOME);
        TestUtilities.mustSetHeight(c9, BuildingType.DOME);
        TestUtilities.mustSetHeight(c4, BuildingType.DOME);
        TestUtilities.mustSetHeight(gameTable.getCell(4, 2), BuildingType.DOME);
        TestUtilities.mustSetHeight(gameTable.getCell(3, 3), BuildingType.DOME);
        TestUtilities.mustSetHeight(gameTable.getCell(3, 4), BuildingType.DOME);
        TestUtilities.mustSetHeight(gameTable.getCell(3, 2), BuildingType.DOME);
        Method m = Player.class.getDeclaredMethod("getBuilderList");
        m.setAccessible(true);
        gameTable.setCurrentBuilder(((ArrayList<Builder>) m.invoke(player1)).get(0));
        c.setBuilder(null);
        player1.setState(BUILDORPASS);
        controller.propertyChange(new PropertyChangeEvent(new Object(), "PASS", null, new News(null, c1)));
        Assertions.assertEquals(0, gameTable.getPlayers().size());

    }

    /**
     * Tests {@link MainController#propertyChange(PropertyChangeEvent)} in the case where there's an OOBException
     *
     * @throws Exception if something fails
     */
    @Test
    void propertyChangeTestOOB() throws Exception {
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
        Field ff = GameTable.class.getDeclaredField("currentPlayer");
        ff.setAccessible(true);
        ff.set(gameTable, -1);
        Assertions.assertDoesNotThrow(() -> controller.propertyChange(new PropertyChangeEvent(new Object(), "MOVE", null, new News())));

    }

    /**
     * Tests {@link MainController#isLegalTurnState(String, ClientState)}
     *
     * @throws Exception if something fails
     */
    @Test
    void isLegalStateTest() throws Exception {
        Method m = MainController.class.getDeclaredMethod("isLegalTurnState", String.class, ClientState.class);
        m.setAccessible(true);
        String stringa = "ASD";
        AtomicInteger count = new AtomicInteger();
        for (ClientState t : ClientState.values()) {
            try {
                m.invoke(null, stringa, t);
                count.addAndGet(1);
            } catch (InvocationTargetException e) {
                if (e.getCause().getClass().equals(IllegalTurnStateException.class)) ;
            }
        }
        Assertions.assertEquals(0, count.get()); // nothing passes
        ArrayList<ClientState> list = new ArrayList<>();
        count.set(0);
        for (ClientState t : ClientState.values()) {
            try {
                m.invoke(null, t.toString(), t);
                count.addAndGet(1);
                list.add(t);
            } catch (InvocationTargetException e) {
                if (e.getCause().getClass().equals(IllegalTurnStateException.class)) ;
            }
        }
        Assertions.assertEquals(2, count.get()); //only BUILD and MOVE have to pass
        Assertions.assertTrue(list.contains(ClientState.BUILD) && list.contains(ClientState.MOVE) && list.size() == 2);

        count.set(0);
        list = new ArrayList<>();
        for (ClientState t : ClientState.values()) {
            try {
                m.invoke(null, "BUILD", t);
                list.add(t);
                count.addAndGet(1);
            } catch (InvocationTargetException e) {
                if (e.getCause().getClass().equals(IllegalTurnStateException.class)) ;
            }
        }
        Assertions.assertEquals(3, count.get()); //only BUILD, MOVEORBUILD and BUILDORPASS have to pass
        Assertions.assertTrue(list.contains(ClientState.BUILD) && list.contains(ClientState.MOVEORBUILD) && list.contains(BUILDORPASS) && list.size() == 3);

        count.set(0);
        list = new ArrayList<>();
        for (ClientState t : ClientState.values()) {
            try {
                m.invoke(null, "PASS", t);
                list.add(t);
                count.addAndGet(1);
            } catch (InvocationTargetException e) {
                if (e.getCause().getClass().equals(IllegalTurnStateException.class)) ;
            }
        }
        Assertions.assertEquals(1, count.get()); //only BUILDORPASS has to pass
        Assertions.assertTrue(list.contains(BUILDORPASS) && list.size() == 1);

        count.set(0);
        list = new ArrayList<>();
        for (ClientState t : ClientState.values()) {
            try {
                m.invoke(null, "MOVE", t);
                list.add(t);
                count.addAndGet(1);
            } catch (InvocationTargetException e) {
                if (e.getCause().getClass().equals(IllegalTurnStateException.class)) ;
            }
        }
        Assertions.assertEquals(2, count.get()); //only MOVEORBUILD and MOVE have to pass
        Assertions.assertTrue(list.contains(ClientState.MOVE) && list.contains(ClientState.MOVEORBUILD) && list.size() == 2);

        count.set(0);
        for (ClientState t : ClientState.values()) {
            try {
                if (t == LOSE || t == INIT || t == WAIT || t == WIN) {
                    m.invoke(null, "ASD", t);
                    count.addAndGet(1);
                }
            } catch (InvocationTargetException e) {
                if (e.getCause().getClass().equals(IllegalTurnStateException.class)) ;
            }
        }
        Assertions.assertEquals(0, count.get()); // nothing passes
    }

    /**
     * Tests {@link MainController#getNews()}
     *
     * @throws Exception if something fails
     */
    @Test
    void testNews() throws Exception {
        MainController mainController = new MainController(new GameTable(2));
        Method m = MainController.class.getDeclaredMethod("getNews");
        m.setAccessible(true);
        Field f = MainController.class.getDeclaredField("news");
        f.setAccessible(true);
        Assertions.assertEquals(f.get(mainController), m.invoke(mainController));
    }

    /**
     * Tests {@link MainController#MainController(GameTable)}
     *
     * @throws Exception if something fails
     */
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

    /**
     * Tests {@link MainController#setGameInitializer(GameInitializer)} )}
     *
     * @throws Exception if something fails
     */
    @Test
    void testGameInitializer() throws Exception {
        Field file = MainController.class.getDeclaredField("gameInitializer");
        file.setAccessible(true);
        MainController mainController = new MainController(new GameTable(2));
        Assertions.assertNull(file.get(mainController));
        Constructor<GameInitializer> m = GameInitializer.class.getDeclaredConstructor(SocketClientConnection.class, SocketClientConnection.class, SocketClientConnection.class, ArrayList.class, Player.class, Player.class, Player.class, GameTable.class, MainController.class);
        m.setAccessible(true);
        GameInitializer gameInitializer1 = m.newInstance(null, null, null, null, null, null, null, null, null);
        mainController.setGameInitializer(gameInitializer1);
        GameInitializer gameInitializer = (GameInitializer) file.get(mainController);
        Assertions.assertNotNull(gameInitializer);
        mainController.setGameInitializer(gameInitializer1);
        Assertions.assertEquals(gameInitializer, file.get(mainController));
    }

    /**
     * Tests {@link MainController#consoleKickPlayer(String)}
     *
     * @throws Exception if something fails
     */
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

    /**
     * Tests {@link MainController#containsPlayer(String)}
     *
     * @throws Exception if something fails
     */
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

    /**
     * Tests {@link MainController#setPlayerFromDisk(String, SocketClientConnection)}
     *
     * @throws Exception if something fails
     */
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

    /**
     * Tests {@link MainController#restartFromDisk(ArrayList)}
     *
     * @throws Exception if something fails
     */
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

    /**
     * Tests {@link MainController#restartFromDisk(ArrayList)}
     *
     * @throws Exception if something fails
     */
    @Test
    void restartFromDiskTest2Players() throws Exception {
        GameTable g = new GameTable(2);
        ArrayList<Player> p = new ArrayList<>();
        p.add(new Player("gigi2", g, null));
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

        SocketClientConnection c2 = new SocketClientConnection(socket2, s);
        SocketClientConnection c1 = new SocketClientConnection(socket3, s);
        Field f2 = SocketClientConnection.class.getDeclaredField("out");
        f2.setAccessible(true);
        f2.set(c1, new ObjectOutputStream(socket1.getOutputStream()));
        f2.set(c2, new ObjectOutputStream(socket2.getOutputStream()));

        ArrayList<SocketClientConnection> list = new ArrayList<>();
        list.add(c1);
        list.add(c2);
        mainController.restartFromDisk(list);
        Field f = SocketClientConnection.class.getDeclaredField("ready");
        f.setAccessible(true);
        Assertions.assertTrue((boolean) f.get(c1));
        Assertions.assertTrue((boolean) f.get(c2));

    }

    /**
     * Tests {@link MainController#getPlayersNumber()}
     *
     * @throws Exception if something fails
     */
    @Test
    void getPlayersNumberTest() throws Exception {
        GameTable g = new GameTable(2);
        ArrayList<Player> p = new ArrayList<>();
        p.add(new Player("gigi2", g, null));
        p.add(new Player("gigi3", g, null));
        g.setPlayers(p);
        MainController m = new MainController(g);
        Assertions.assertEquals(2, m.getPlayersNumber());
        ArrayList<Player> p1 = new ArrayList<>();
        GameTable g1 = new GameTable(3);
        p1.add(new Player("gigi4", g1, null));
        p1.add(new Player("gigi5", g1, null));
        p1.add(new Player("gigi6", g1, null));
        g1.setPlayers(p1);
        MainController m1 = new MainController(g1);
        Assertions.assertEquals(3, m1.getPlayersNumber());
        Assertions.assertEquals(2, m.getPlayersNumber());
    }

    /**
     * Tests the PASS case and the {@link MainController#saveGameState()} that follows
     *
     * @throws Exception if something fails
     */
    @Test
    void propertyChangeTest3() throws Exception {
        Socket socket = new Socket();

        Field a = GameTable.class.getDeclaredField("news");
        Field b = GameTable.class.getDeclaredField("type");
        a.setAccessible(true);
        b.setAccessible(true);
        Field f = ServerMain.class.getDeclaredField("persistence");
        f.setAccessible(true);
        f.set(null, true);
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
        Method m = Player.class.getDeclaredMethod("getBuilderList");
        m.setAccessible(true);
        gameTable.setCurrentBuilder(((ArrayList<Builder>) m.invoke(player2)).get(0));
        News news = new News("ASD", c2);
        player2.setState(BUILDORPASS);
        int gameIndex = gameTable.getGameIndex();
        Path file1 = Paths.get("game" + gameIndex + ".save");
        byte[] before = Files.readAllBytes(file1);
        controller.propertyChange(new PropertyChangeEvent(new Object(), "PASS", null, news));
        Assertions.assertEquals(gameTable.getCurrentPlayer(), player1);
        byte[] after = Files.readAllBytes(file1);
        Assertions.assertNotEquals(before, after);


    }

    /**
     * Same as {@link MainControllerTest#propertyChangeTest3()} but with persistence disabled
     *
     * @throws Exception if something fails
     */
    @Test
    void propertyChangeTest4() throws Exception {
        Socket socket = new Socket();

        Field a = GameTable.class.getDeclaredField("news");
        Field b = GameTable.class.getDeclaredField("type");
        a.setAccessible(true);
        b.setAccessible(true);
        Field f = ServerMain.class.getDeclaredField("persistence");
        f.setAccessible(true);
        f.set(null, false);
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
        Method m = Player.class.getDeclaredMethod("getBuilderList");
        m.setAccessible(true);
        gameTable.setCurrentBuilder(((ArrayList<Builder>) m.invoke(player2)).get(0));
        News news = new News("ASD", c2);
        player2.setState(BUILDORPASS);
        Field f5 = MainController.class.getDeclaredField("fileOutputStream");
        f5.setAccessible(true);
        Assertions.assertNull(f5.get(controller));
        controller.propertyChange(new PropertyChangeEvent(new Object(), "PASS", null, news));
        Assertions.assertEquals(gameTable.getCurrentPlayer(), player1);
        Assertions.assertNull(f5.get(controller));
    }

    /**
     * Tests the BUILD and the MOVE cases
     *
     * @throws Exception if something fails
     */
    @Test
    void propertyChangeTest5() throws Exception {
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
        News news = new News("ASD", c2);
        player2.setState(MOVE);
        news.setCoords(4, 4, 1);
        controller.propertyChange(new PropertyChangeEvent(new Object(), "MOVE", null, news));
        Assertions.assertEquals("MOVEKO", b.get(gameTable));
        player2.setState(BUILD);
        news.setCoords(4, 4, 1);
        controller.propertyChange(new PropertyChangeEvent(new Object(), "BUILD", null, news));
        Assertions.assertEquals("BUILDKO", b.get(gameTable));
    }

    /**
     * Tests the the {@link MainController#saveGameState()} function in the exception case
     *
     * @throws Exception if something fails
     */
    @Test
    void saveGameStateTest() throws Exception {
        Method m = MainController.class.getDeclaredMethod("saveGameState");
        m.setAccessible(true);
        MainController controller = new MainController(null);
        Assertions.assertDoesNotThrow(() -> m.invoke(controller));
    }


}