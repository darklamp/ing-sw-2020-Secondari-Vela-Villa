package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;
import it.polimi.ingsw.Network.Server;
import it.polimi.ingsw.Network.SocketClientConnection;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.net.Socket;
import java.util.ArrayList;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

class GameTableTest {

    @Test
    void nextTurn() {
    }

    @Test
    public void resetMoveTimerTest() throws Exception {
        GameTable g = new GameTable(2);
        g.resetMoveTimer();
        g.resetMoveTimer();
        Field f = GameTable.class.getDeclaredField("timerThread");
        f.setAccessible(true);
        Thread thread = (Thread) f.get(g);
        Field f1 = GameTable.class.getDeclaredField("type");
        f1.setAccessible(true);
        Field f2 = GameTable.class.getDeclaredField("exit");
        f2.setAccessible(true);
        f2.set(g, true);
        thread.interrupt();
        sleep(10);
        assertEquals("ABORT", f1.get(g));
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
        c3.send(player3);
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
        gameTable.setGods(choices);

        gameTable.removePlayer(player2, true);
        assertNull(gameTable.getCurrentBuilder());
        assertEquals(player3.getFirstState(), player3.getState());
        assertEquals(2, gameTable.getPlayerConnections().size());
        gameTable.removePlayer(player3, true);
        assertEquals(1, gameTable.getPlayerConnections().size());
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