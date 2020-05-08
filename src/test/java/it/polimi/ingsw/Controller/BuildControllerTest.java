package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertSame;

class BuildControllerTest {

    @Test
    void handleBuildTest() throws Exception {
        Field a = GameTable.class.getDeclaredField("news");
        a.setAccessible(true);
        GameTable gameTable = new GameTable(2);
        News news = new News();
        news.setCoords(4, 4, 0);
        a.set(gameTable, news);
        ArrayList<Integer> choices = new ArrayList<>();
        choices.add(1);
        choices.add(2);
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
        gameTable.setGods(choices);
        Field b = Player.class.getDeclaredField("turnState");

        b.setAccessible(true);
        b.set(player2, player2.getFirstState());

        BuildController buildController = new BuildController(gameTable);
        buildController.handleBuild(news);
        assertSame(BuildingType.NONE, gameTable.getCell(4, 4).getHeight());

        news.setCoords(2, 1, 0);
        Field c = Player.class.getDeclaredField("builderList");
        c.setAccessible(true);
        ArrayList<Builder> bs = (ArrayList<Builder>) c.get(player2);
        gameTable.setCurrentBuilder(bs.get(0));
        buildController.handleBuild(news);
        assertSame(BuildingType.BASE, gameTable.getCell(2, 1).getHeight());

    }
}