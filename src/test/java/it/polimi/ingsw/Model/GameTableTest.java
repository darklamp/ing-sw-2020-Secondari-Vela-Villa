package it.polimi.ingsw.Model;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;
import it.polimi.ingsw.Network.Server;
import it.polimi.ingsw.Network.SocketClientConnection;
import it.polimi.ingsw.TestUtilities;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

class GameTableTest {

    @Test
    void getGameStateTest() throws Exception {
        GameTable g = new GameTable(3);
        SocketClientConnection c1 = new SocketClientConnection(new Socket(), null);
        SocketClientConnection c2 = new SocketClientConnection(new Socket(), null);
        SocketClientConnection c3 = new SocketClientConnection(new Socket(), null);


        Player player1 = new Player("gigi", g, c1);
        Player player2 = new Player("gigi2", g, c2);
        Player player3 = new Player("gigi3", g, c3);
        ArrayList<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        g.setPlayers(players);
        player1.setState(ClientState.WAIT);
        assertEquals(ClientState.WAIT, g.getGameState().get((short) 0));
        assertNull(g.getGameState().get((short) 1));
        assertNull(g.getGameState().get((short) 2));
        player2.setState(ClientState.WAIT);
        assertEquals(ClientState.WAIT, g.getGameState().get((short) 0));
        assertEquals(ClientState.WAIT, g.getGameState().get((short) 1));
        assertEquals(player2.getNickname(), g.getGameState().getCurrentPlayer());
        assertEquals(player1.getNickname(), g.getGameState().getName(0));
        assertEquals(player3.getNickname(), g.getGameState().getName(2));
        assertEquals(player2.getNickname(), g.getGameState().getName(1));
        ArrayList<Player> players1 = TestUtilities.getAccessibleField("players", g);

        players1.remove(1);
        assertEquals(player3.getNickname(), g.getGameState().getName(1));
        assertNull(g.getGameState().get((short) 2));
        assertNull(g.getGameState().getName(2));
        players1.remove(1);
        assertEquals(ClientState.LOSE, g.getGameState().get((short) 1));
        assertNull(g.getGameState().getName(2));

    }

    /**
     * Tests reaction of time thread to an interruption.
     *
     * @throws Exception aa
     */
    @Test
    public void resetMoveTimerTest() throws Exception {
        GameTable g = new GameTable(2);
        g.resetMoveTimer();
        g.resetMoveTimer();
        Thread thread = TestUtilities.getAccessibleField("timerThread", g);
        Field f1 = GameTable.class.getDeclaredField("type");
        f1.setAccessible(true);
        Field f2 = GameTable.class.getDeclaredField("exit");
        f2.setAccessible(true);
        f2.set(g, true);
        sleep(10);
        thread.interrupt();
        sleep(100);
        assertEquals("ABORT", f1.get(g));
    }


    /**
     * Tests what happens if timer expires
     *
     * @throws Exception aa
     */
    @Test
    public void resetMoveTimerTest2() throws Exception {
        GameTable g = new GameTable(2);
        g.resetMoveTimer();
        Field s = Server.class.getDeclaredField("moveTimerTimeUnit");
        s.setAccessible(true);
        s.set(null, TimeUnit.MILLISECONDS);
        Field s1 = Server.class.getDeclaredField("moveTimer");
        s1.setAccessible(true);
        s1.set(null, (short) 100);
        g.resetMoveTimer();
        sleep(200);
        assertEquals("ABORT", TestUtilities.getAccessibleField("type", g));
    }

    @Test
    public synchronized void removePlayerTest() throws Exception {
        GameTable gameTable = new GameTable(3);
        ArrayList<Integer> choices = new ArrayList<>();
        choices.add(1);
        choices.add(2);
        choices.add(3);
        SocketClientConnection c1 = new SocketClientConnection(new Socket(), null);
        SocketClientConnection c2 = new SocketClientConnection(new Socket(), null);
        SocketClientConnection c3 = new SocketClientConnection(new Socket(), null);


        Player player1 = new Player("gigi", gameTable, c1);
        Player player2 = new Player("gigi2", gameTable, c2);
        Player player3 = new Player("gigi3", gameTable, c3);
        c1.setPlayer(player1);
        c2.setPlayer(player2);
        c3.setPlayer(player3);
        player1.setGod(choices.get(0));
        player2.setGod(choices.get(1));
        player3.setGod(choices.get(2));
        try {
            player1.initBuilderList(gameTable.getCell(2, 2));
            player1.initBuilderList(gameTable.getCell(2, 3));
            player2.initBuilderList(gameTable.getCell(1, 2));
            player2.initBuilderList(gameTable.getCell(1, 1));
            player3.initBuilderList(gameTable.getCell(4, 2));
            player3.initBuilderList(gameTable.getCell(4, 1));
        } catch (InvalidBuildException | InvalidCoordinateException e) {
            e.printStackTrace();
        }
        ArrayList<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        gameTable.setPlayers(players);
        gameTable.setCurrentBuilder(player3.getBuilderList().get(0));
        gameTable.nextTurn();

        gameTable.removePlayer(player3, true);
        int currentPlayer = TestUtilities.getAccessibleField("currentPlayer", gameTable);
        assertEquals(0, currentPlayer);
        assertNull(gameTable.getCurrentBuilder());
        assertEquals(player1.getFirstState(), player1.getState());
        assertEquals(2, gameTable.getPlayerConnections().size());
        gameTable.removePlayer(player1, true);
        assertEquals(1, gameTable.getPlayerConnections().size());
    }

    @Test
    public synchronized void removePlayerTest2() throws Exception {
        GameTable gameTable = new GameTable(3);
        ArrayList<Integer> choices = new ArrayList<>();
        choices.add(1);
        choices.add(2);
        choices.add(3);
        SocketClientConnection c1 = new SocketClientConnection(new Socket(), null);
        SocketClientConnection c2 = new SocketClientConnection(new Socket(), null);
        SocketClientConnection c3 = new SocketClientConnection(new Socket(), null);


        Player player1 = new Player("gigi", gameTable, c1);
        Player player2 = new Player("gigi2", gameTable, c2);
        Player player3 = new Player("gigi3", gameTable, c3);
        c1.setPlayer(player1);
        c2.setPlayer(player2);
        c3.setPlayer(player3);
        player1.setGod(choices.get(0));
        player2.setGod(choices.get(1));
        player3.setGod(choices.get(2));
        try {
            player1.initBuilderList(gameTable.getCell(2, 2));
            player1.initBuilderList(gameTable.getCell(2, 3));
            player2.initBuilderList(gameTable.getCell(1, 2));
            player2.initBuilderList(gameTable.getCell(1, 1));
            player3.initBuilderList(gameTable.getCell(4, 2));
            player3.initBuilderList(gameTable.getCell(4, 1));
        } catch (InvalidBuildException | InvalidCoordinateException e) {
            e.printStackTrace();
        }
        ArrayList<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        gameTable.setPlayers(players);
        gameTable.setCurrentBuilder(player2.getBuilderList().get(0));

        gameTable.removePlayer(player2, true);
        int currentPlayer = TestUtilities.getAccessibleField("currentPlayer", gameTable);
        assertEquals(1, currentPlayer);
    }

    @Test
    public synchronized void closeGameTest() throws Exception {
        GameTable gameTable = new GameTable(3);
        ArrayList<Integer> choices = new ArrayList<>();
        choices.add(1);
        choices.add(2);
        choices.add(3);
        SocketClientConnection c1 = new SocketClientConnection(new Socket(), null);
        SocketClientConnection c2 = new SocketClientConnection(new Socket(), null);
        SocketClientConnection c3 = new SocketClientConnection(new Socket(), null);


        Player player1 = new Player("gigi", gameTable, c1);
        Player player2 = new Player("gigi2", gameTable, c2);
        Player player3 = new Player("gigi3", gameTable, c3);
        c1.setPlayer(player1);
        c2.setPlayer(player2);
        c3.setPlayer(player3);
        player1.setGod(choices.get(0));
        player2.setGod(choices.get(1));
        player3.setGod(choices.get(2));
        try {
            player1.initBuilderList(gameTable.getCell(2, 2));
            player1.initBuilderList(gameTable.getCell(2, 3));
            player2.initBuilderList(gameTable.getCell(1, 2));
            player2.initBuilderList(gameTable.getCell(1, 1));
            player3.initBuilderList(gameTable.getCell(4, 2));
            player3.initBuilderList(gameTable.getCell(4, 1));
        } catch (InvalidBuildException | InvalidCoordinateException e) {
            e.printStackTrace();
        }
        ArrayList<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        gameTable.setPlayers(players);
        gameTable.setCurrentBuilder(player2.getBuilderList().get(0));
        assertDoesNotThrow(gameTable::closeGame);
        assert gameTable.getPlayers().size() == 0;
    }

    @Test
    public void getCellTest() throws Exception {
        SocketClientConnection c = new SocketClientConnection(new Socket(), new Server());
        GameTable g = new GameTable(2);
        Player player1 = new Player("gigi", g, c);
        player1.setGod(0);

        player1.initBuilderList(g.getCell(2, 2));
        player1.initBuilderList(g.getCell(2, 3));
        c.setPlayer(player1);
        assertEquals(g.getCell(2, 2), g.getCell(c));
    }

    @Test
    public void getPlayerIndexTest() throws Exception {
        GameTable gameTable = new GameTable(3);
        ArrayList<Integer> choices = new ArrayList<>();
        choices.add(1);
        choices.add(2);
        choices.add(3);
        SocketClientConnection c1 = new SocketClientConnection(new Socket(), null);
        SocketClientConnection c2 = new SocketClientConnection(new Socket(), null);
        SocketClientConnection c3 = new SocketClientConnection(new Socket(), null);


        Player player1 = new Player("gigi", gameTable, c1);
        Player player2 = new Player("gigi2", gameTable, c2);
        Player player3 = new Player("gigi3", gameTable, c3);
        Player player4 = new Player("gigi4", gameTable, c3);

        c1.setPlayer(player1);
        c2.setPlayer(player2);
        c3.setPlayer(player3);
        player1.setGod(choices.get(0));
        player2.setGod(choices.get(1));
        player3.setGod(choices.get(2));
        try {
            player1.initBuilderList(gameTable.getCell(2, 2));
            player1.initBuilderList(gameTable.getCell(2, 3));
            player2.initBuilderList(gameTable.getCell(1, 2));
            player2.initBuilderList(gameTable.getCell(1, 1));
            player3.initBuilderList(gameTable.getCell(4, 2));
            player3.initBuilderList(gameTable.getCell(4, 1));
        } catch (InvalidBuildException | InvalidCoordinateException e) {
            e.printStackTrace();
        }
        ArrayList<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        gameTable.setPlayers(players);
        // gameTable.setCurrentBuilder(player2.getBuilderList().get(0));
        assert gameTable.getPlayerIndex(player1) == 0;
        assert gameTable.getPlayerIndex(player2) == 1;
        assert gameTable.getPlayerIndex(player3) == 2;
        assert gameTable.getPlayerIndex(player4) == -1;


    }

    @Test
    public void isInvalidCoordinateTest() throws Exception {
        assertThrows(InvalidCoordinateException.class, () -> GameTable.isInvalidCoordinate(5, 0));
        assertThrows(InvalidCoordinateException.class, () -> GameTable.isInvalidCoordinate(-1, 0));
        assertThrows(InvalidCoordinateException.class, () -> GameTable.isInvalidCoordinate(3, 5));
        assertThrows(InvalidCoordinateException.class, () -> GameTable.isInvalidCoordinate(3, -1));
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                int finalI = i;
                int finalJ = j;
                assertDoesNotThrow(() -> GameTable.isInvalidCoordinate(finalI, finalJ));
            }
        }
    }


}