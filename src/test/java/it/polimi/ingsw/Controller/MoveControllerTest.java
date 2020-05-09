package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Model.Builder;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.News;
import it.polimi.ingsw.Model.Player;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertSame;

class MoveControllerTest {

    @Test
    void handleMoveTest() throws Exception {
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
        player3.setGod(1); /* tests ArtemisException */
        player3.initBuilderList(gameTable.getCell(3, 3));
        player3.initBuilderList(gameTable.getCell(3, 4));
        players.remove(player2);
        players.add(player3);
        gameTable.setPlayers(players);

        ArrayList<Builder> bd = (ArrayList<Builder>) c.get(player3);
        Builder bc = bd.get(0);
        gameTable.setCurrentBuilder(bc);
        news.setCoords(4, 4, 0);
        moveController.handleMove(news);
        assertSame(bc, gameTable.getCell(4, 4).getBuilder());

    }
}