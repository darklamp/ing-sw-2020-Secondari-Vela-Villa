package it.polimi.ingsw.Model;

import it.polimi.ingsw.Client.ClientState;
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
        Field f = GameTable.class.getDeclaredField("players");
        f.setAccessible(true);
        ((ArrayList<Player>) f.get(g)).remove(1);
        assertEquals(player3.getNickname(), g.getGameState().getName(1));
        assertNull(g.getGameState().get((short) 2));
        assertNull(g.getGameState().getName(2));
        ((ArrayList<Player>) f.get(g)).remove(1);
        assertEquals(ClientState.LOSE, g.getGameState().get((short) 1));
        assertNull(g.getGameState().getName(2));


        /*public GameStateMessage getGameState() {
            return new GameStateMessage(players.get(0).getState(), players.size() == 1 ? ClientState.LOSE : players.get(1).getState(), players.size() == 3 ? players.get(2).getState() : null, players.get(0).getNickname(), players.size() == 1 ? null : players.get(1).getNickname(), players.size() == 3 ? players.get(2).getNickname() : null, currentPlayer);
        }*/

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
        sleep(100);
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
        gameTable.setCurrentBuilder(player3.getBuilderList().get(0));
        gameTable.nextTurn();

        gameTable.removePlayer(player3, true);
        Field f3 = GameTable.class.getDeclaredField("currentPlayer");
        f3.setAccessible(true);
        assertEquals(0, f3.get(gameTable));
        assertNull(gameTable.getCurrentBuilder());
        assertEquals(player1.getFirstState(), player1.getState());
        assertEquals(2, gameTable.getPlayerConnections().size());
        gameTable.removePlayer(player1, true);
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