package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.News;
import it.polimi.ingsw.Model.Player;
import it.polimi.ingsw.Network.SocketClientConnection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.ArrayList;

class MainControllerTest {

    @Test
    void propertyChangeTest() throws Exception{

        Field a = GameTable.class.getDeclaredField("news");
        Field b = GameTable.class.getDeclaredField("type");
        a.setAccessible(true);
        b.setAccessible(true);
        GameTable gameTable = new GameTable(2);
        MainController controller= new MainController(gameTable);
        News news = new News();
        news.setInvalid();
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
        controller.propertyChange(new PropertyChangeEvent(new Object(),"ciao",null,news));
        News news2 = (News) a.get(gameTable);
        Assertions.assertEquals(news2.isValid(),false);
        String stringa = (String) b.get(gameTable);
        Assertions.assertEquals(stringa,"INVALIDNEWS");
        News news4 = new News();  //da controllare
        controller.propertyChange(new PropertyChangeEvent(new Object(),"ABORT",null,news4));
        stringa = (String) b.get(gameTable);
        Assertions.assertEquals(stringa,"ABORT");
        News news3= new News("abc", new SocketClientConnection(new Socket(), null));
        controller.propertyChange(new PropertyChangeEvent(new Object(),"ciao",null,news3));
        Assertions.assertEquals(stringa,"NOTYOURTURN");

        controller.propertyChange(new PropertyChangeEvent(new Object(),"PLAYERTIMEOUT",null,news4));
        stringa = (String) b.get(gameTable);
        Assertions.assertEquals(stringa,"PLAYERTIMEOUT");




        /*news.setCoords(4, 4, 0);
        a.set(gameTable, news);

        Field b = Player.class.getDeclaredField("turnState");*/


    }
}