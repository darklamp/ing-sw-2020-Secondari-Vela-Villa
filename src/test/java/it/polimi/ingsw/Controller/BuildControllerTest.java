package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;
import it.polimi.ingsw.Network.Server;
import it.polimi.ingsw.Network.SocketClientConnection;
import it.polimi.ingsw.TestUtilities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.net.Socket;
import java.util.ArrayList;

import static it.polimi.ingsw.Client.ClientState.BUILD;
import static it.polimi.ingsw.Model.BuildingType.NONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class BuildControllerTest {

    @Test
    void handleBuildTest() throws Exception {
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
        Field d = Player.class.getDeclaredField("connection");
        d.setAccessible(true);
        News news = new News("ASD", (SocketClientConnection) d.get(player1));
        news.setCoords(4, 4, 0);
        a.set(gameTable, news);
        b.setAccessible(true);
        b.set(player2, player2.getFirstState());

        BuildController buildController = new BuildController(gameTable);
        buildController.handleBuild(news);
        assertSame(NONE, gameTable.getCell(4, 4).getHeight());

        news.setCoords(2, 1, 0);
        Field c = Player.class.getDeclaredField("builderList");
        c.setAccessible(true);
        //noinspection unchecked
        ArrayList<Builder> bs = (ArrayList<Builder>) c.get(player2);
        gameTable.setCurrentBuilder(bs.get(0));
        player2.setState(BUILD);
        buildController.handleBuild(news);
        assertSame(BuildingType.BASE, gameTable.getCell(2, 1).getHeight());
        Player player3 = new Player("gigg2", gameTable, null);
        player3.setGod(4);
        player3.initBuilderList(gameTable.getCell(3, 3));
        player3.initBuilderList(gameTable.getCell(4, 3));
        players.remove(player2);
        players.add(player3);
        gameTable.setPlayers(players);
        b.set(player3, BUILD);
        news.setCoords(3, 4, 0);
        Field f = GameTable.class.getDeclaredField("currentPlayer");
        f.setAccessible(true);
        f.set(gameTable, 2);
        //noinspection unchecked
        ArrayList<Builder> bg = (ArrayList<Builder>) c.get(player3);
        gameTable.setCurrentBuilder(bg.get(0));
        buildController.handleBuild(news);
        assertSame(BuildingType.BASE, gameTable.getCell(3, 4).getHeight());

        Player player4 = new Player("gigg3", gameTable, null);
        player4.setGod(8);
        player4.initBuilderList(gameTable.getCell(0, 3));
        player4.initBuilderList(gameTable.getCell(0, 4));
        players.remove(player3);
        players.add(player4);
        player4.setState(BUILD);

        f.set(gameTable, 3);
        gameTable.setPlayers(players);
        news.setCoords(0, 2, 0);
        //noinspection unchecked
        ArrayList<Builder> bz = (ArrayList<Builder>) c.get(player4);
        gameTable.setCurrentBuilder(bz.get(0));
        buildController.handleBuild(news);
        assertSame(BuildingType.BASE, gameTable.getCell(0, 2).getHeight());


    }

    @Test
    void edgeCasesTest() throws Exception {
        Field a = GameTable.class.getDeclaredField("news");
        a.setAccessible(true);
        GameTable gameTable = new GameTable(2);

        ArrayList<Integer> choices = new ArrayList<>();
        choices.add(1);
        choices.add(2);
        Player player1 = new Player("gigi", gameTable, new SocketClientConnection(new Socket(), new Server()));
        Player player2 = new Player("gigi2", gameTable, new SocketClientConnection(new Socket(), new Server()));
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
        Field b = Player.class.getDeclaredField("turnState");
        Field d = Player.class.getDeclaredField("connection");
        d.setAccessible(true);
        News news = new News("ASD", (SocketClientConnection) d.get(player2));
        news.setCoords(4, 4, 0);
        a.set(gameTable, news);
        b.setAccessible(true);
        b.set(player2, player2.getFirstState());

        BuildController buildController = new BuildController(gameTable);
        Field f2 = Player.class.getDeclaredField("builderList");
        f2.setAccessible(true);
        Builder b2 = ((ArrayList<Builder>) f2.get(player2)).get(1);
        Field f3 = GameTable.class.getDeclaredField("currentBuilder");
        f3.setAccessible(true);
        f3.set(gameTable, b2);
        player2.setState(BUILD);
        buildController.handleBuild(news);
        assertEquals(NONE, gameTable.getCell(4, 4).getHeight());
    }

    @Test
    void buildBeforeMoveTest() throws Exception {
        Field a = GameTable.class.getDeclaredField("news");
        a.setAccessible(true);
        GameTable gameTable = new GameTable(2);

        ArrayList<Integer> choices = new ArrayList<>();
        choices.add(1);
        choices.add(8);
        Player player1 = new Player("gigi", gameTable, null);
        Player player2 = new Player("gigi2", gameTable, null);
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

        Field d = Player.class.getDeclaredField("connection");
        d.setAccessible(true);
        News news = new News("BUILD", (SocketClientConnection) d.get(player2));
        BuildController buildController = new BuildController(gameTable);
        ArrayList<Builder> players1 = TestUtilities.getAccessibleField("builderList", player2);
        Field f3 = GameTable.class.getDeclaredField("currentBuilder");
        f3.setAccessible(true);
        f3.set(gameTable, null);
        gameTable.setCurrentBuilder(null);
        player2.setState(BUILD);
        news.setCoords(0, 1, 1);
        buildController.handleBuild(news);
        Assertions.assertSame(null, gameTable.getCurrentBuilder());
    }
}