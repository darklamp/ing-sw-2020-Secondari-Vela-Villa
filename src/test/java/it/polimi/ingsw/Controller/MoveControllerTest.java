package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;
import it.polimi.ingsw.Model.Exceptions.WinnerException;
import it.polimi.ingsw.Network.Server;
import it.polimi.ingsw.Network.SocketClientConnection;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.net.Socket;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MoveControllerTest {

    /**
     * Tests basic move cases.
     *
     * @throws Exception aa
     */
    @Test
    void handleMoveTest() throws Exception {
        Field a = GameTable.class.getDeclaredField("news");
        a.setAccessible(true);
        GameTable gameTable = new GameTable(2);

        ArrayList<Integer> choices = new ArrayList<>();
        choices.add(1);
        choices.add(2);
        Player player1 = new Player("gigi", gameTable, new SocketClientConnection(new Socket(), new Server()));
        Player player2 = new Player("gigi2", gameTable, null);
        player1.setGod(choices.get(0));
        player2.setGod(choices.get(1));
        Field d = Player.class.getDeclaredField("connection");
        d.setAccessible(true);
        News news = new News("ASD", (SocketClientConnection) d.get(player1));
        news.setCoords(4, 4, 0);
        a.set(gameTable, news);
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
        Field b = Player.class.getDeclaredField("turnState");

        b.setAccessible(true);
        b.set(player2, player2.getFirstState());

        MoveController moveController = new MoveController(gameTable);
        moveController.handleMove(news);
        assertSame(null, gameTable.getCell(4, 4).getBuilder());

        news.setCoords(2, 1, 0);
        Field c = Player.class.getDeclaredField("builderList");
        c.setAccessible(true);
        //noinspection unchecked
        ArrayList<Builder> bs = (ArrayList<Builder>) c.get(player2);
        Builder bb = bs.get(0);
        gameTable.setCurrentBuilder(bb);
        moveController.handleMove(news);
        assertSame(bb, gameTable.getCell(2, 1).getBuilder());

        Player player3 = new Player("giggi2", gameTable, null);
        player3.setGod(1);
        player3.initBuilderList(gameTable.getCell(3, 3));
        player3.initBuilderList(gameTable.getCell(3, 4));
        players.remove(player2);
        players.add(player3);
        gameTable.setPlayers(players);

        ArrayList<Builder> bd = (ArrayList<Builder>) c.get(player3);
        Builder bc = bd.get(0);
        gameTable.setCurrentBuilder(bc);
        news.setCoords(4, 4, 0);
        player3.setState(ClientState.MOVE);
        moveController.handleMove(news);
        assertSame(bc, gameTable.getCell(4, 4).getBuilder());

        Player player4 = new Player("giggi4", gameTable, null);
        players.clear();
        players.add(player1);
        players.add(player4);
        gameTable.setPlayers(players);
        player4.setGod(6);
        gameTable.getCell(3, 3).setBuilder(null);
        gameTable.getCell(1, 3).setBuilder(null);

        player4.initBuilderList(gameTable.getCell(3, 3));
        player4.initBuilderList(gameTable.getCell(1, 4));

        ArrayList<Builder> bg = (ArrayList<Builder>) c.get(player4);
        Builder bk = bg.get(0);
        gameTable.setCurrentBuilder(bk);
        news.setCoords(2, 3, 0);
        moveController.handleMove(news);
        assertSame(bk, gameTable.getCell(2, 3).getBuilder());

    }

    /**
     * Tests basic move cases part 2.
     *
     * @throws Exception aa
     */
    @Test
    void handleMoveTest2() throws Exception {
        Field a = GameTable.class.getDeclaredField("news");
        a.setAccessible(true);
        GameTable gameTable = new GameTable(2);

        ArrayList<Integer> choices = new ArrayList<>();
        choices.add(1);
        choices.add(2);
        Player player1 = new Player("gigi", gameTable, new SocketClientConnection(new Socket(), new Server()));
        Player player2 = new Player("gigi2", gameTable, null);
        player1.setGod(choices.get(0));
        player2.setGod(choices.get(1));
        Field d = Player.class.getDeclaredField("connection");
        d.setAccessible(true);
        News news = new News("ASD", (SocketClientConnection) d.get(player1));
        news.setCoords(4, 4, 0);
        a.set(gameTable, news);
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
        Field f1 = Player.class.getDeclaredField("builderList");
        f1.setAccessible(true);
        Builder b = ((ArrayList<Builder>) f1.get(player2)).get(1);
        gameTable.setCurrentBuilder(b);
        MoveController moveController = new MoveController(gameTable);
        Field f2 = GameTable.class.getDeclaredField("type");
        f2.setAccessible(true);
        moveController.handleMove(news);
        assertNull(gameTable.getCell(4, 4).getBuilder());
    }

    /**
     * Tests for Pan specific case handling.
     *
     * @throws Exception aa
     */
    @Test
    void panExcTest() throws Exception {
        GameTable gameTable = new GameTable(2);

        ArrayList<Integer> choices = new ArrayList<>();
        choices.add(1);
        choices.add(7);
        Player player1 = new Player("gigi", gameTable, new SocketClientConnection(new Socket(), new Server()));
        Player player2 = new Player("gigi2", gameTable, new SocketClientConnection(new Socket(), new Server()));
        player1.setGod(choices.get(0));
        player2.setGod(choices.get(1));
        Field d = Player.class.getDeclaredField("connection");
        d.setAccessible(true);
        News news = new News("ASD", (SocketClientConnection) d.get(player2));
        news.setCoords(0, 0, 1);
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
        Field ff = Cell.class.getDeclaredField("height");
        ff.setAccessible(true);
        ff.set(gameTable.getCell(0, 0), BuildingType.NONE);
        ff.set(gameTable.getCell(1, 1), BuildingType.TOP);
        MoveController moveController = new MoveController(gameTable);

        assertThrows(WinnerException.class, () -> moveController.handleMove(news));
    }

    /**
     * Tests for Artemis specific case handling.
     *
     * @throws Exception aa
     */
    @Test
    void artemisExcTest() throws Exception {
        GameTable gameTable = new GameTable(2);

        ArrayList<Integer> choices = new ArrayList<>();
        choices.add(0);
        choices.add(1);
        Player player1 = new Player("gigi", gameTable, new SocketClientConnection(new Socket(), new Server()));
        Player player2 = new Player("gigi2", gameTable, new SocketClientConnection(new Socket(), new Server()));
        player1.setGod(choices.get(0));
        player2.setGod(choices.get(1));
        Field d = Player.class.getDeclaredField("connection");
        d.setAccessible(true);
        News news = new News("ASD", (SocketClientConnection) d.get(player2));
        news.setCoords(0, 0, 1);
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
        Field ff = Cell.class.getDeclaredField("height");
        ff.setAccessible(true);
        ff.set(gameTable.getCell(0, 0), BuildingType.NONE);
        ff.set(gameTable.getCell(1, 1), BuildingType.TOP);
        MoveController moveController = new MoveController(gameTable);
        player1.setFirstTime(true);
        player2.setFirstTime(true);
        moveController.handleMove(news);
        assertEquals(ClientState.MOVEORBUILD, gameTable.getCurrentPlayer().getState());
    }
}