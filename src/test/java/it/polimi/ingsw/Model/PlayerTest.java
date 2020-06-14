package it.polimi.ingsw.Model;

import it.polimi.ingsw.BadGod;
import it.polimi.ingsw.BadGod2;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.NickAlreadyTakenException;
import it.polimi.ingsw.Model.Exceptions.NoMoreMovesException;
import it.polimi.ingsw.Network.Server;
import it.polimi.ingsw.Network.SocketClientConnection;
import it.polimi.ingsw.TestUtilities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static it.polimi.ingsw.Client.ClientState.*;

class  PlayerTest {

    @Test
    void initBuilderList() throws Exception {
        GameTable g = new GameTable(2);
        Player p1 = new Player("Giggino", g, "MINOTAUR");
        Player p2 = new Player("Giggino2", g, "ATLAS");
        Cell c1 = g.getCell(4,3);
        Cell c2 = g.getCell(4,4);
        Cell c3 = g.getCell(4,1);
        Cell c4 = g.getCell(4, 2);
        Cell c5 = g.getCell(3, 2);
        p2.initBuilderList(c2);
        p2.initBuilderList(c3);


        Assertions.assertThrows(InvalidBuildException.class, () -> {
            p1.initBuilderList(c2);  //parte l'eccezione perchè la cella c2 è occupata dal player p2
        });
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            p2.initBuilderList(c4); // anche se è libera la cella parte l'eccezione perchè p2 ha già due builder posizionati
        });
        Field f5 = Player.class.getDeclaredField("god");
        f5.setAccessible(true);
        f5.set(p1, null);

        Assertions.assertThrows(InvalidBuildException.class, () -> {
            p1.initBuilderList(c4); // the builder's god attribute is null
        });


        Player p3 = new Player("ALE", g, new SocketClientConnection(new Socket(), new Server()));
        p3.setGod(5);
        Assertions.assertDoesNotThrow(() -> {
            p3.initBuilderList(c4);
            p3.initBuilderList(c1);
        });
        Assertions.assertTrue(p3.getBuilderList().get(0) instanceof Hephaestus && p3.getBuilderList().get(1) instanceof Hephaestus);

        Player p4 = new Player("ALE1", g, new SocketClientConnection(new Socket(), new Server()));
        Field f = Player.class.getDeclaredField("god");
        f.setAccessible(true);
        f.set(p4, "peppe");
        Assertions.assertThrows(InvalidBuildException.class, () -> p4.initBuilderList(c5));
    }

    /**
     * Tests the MOVEORBUILD branch in {@link Player#checkConditions(Builder)}
     *
     * @throws Exception aa
     */
    @Test
    void checkConditionsTest1() throws Exception {
        GameTable g = new GameTable(2);
        Player p1 = new Player("Giggino", g, "PROMETHEUS");
        Player p2 = new Player("Giggino2", g, "ATLAS");
        TestUtilities.mustSetHeight(g.getCell(1, 0), BuildingType.DOME);
        TestUtilities.mustSetHeight(g.getCell(1, 1), BuildingType.DOME);
        TestUtilities.mustSetHeight(g.getCell(1, 2), BuildingType.DOME);
        p1.initBuilderList(g.getCell(0, 0));
        p1.initBuilderList(g.getCell(0, 1));
        p2.initBuilderList(g.getCell(0, 2));
        p1.setState(MOVEORBUILD);
        Assertions.assertThrows(NoMoreMovesException.class, () -> p1.checkConditions(p1.getBuilderList().get(0)));
        p1.setState(MOVEORBUILD);
        g.getCell(0, 1).setBuilder(null);
        TestUtilities.mustSetHeight(g.getCell(0, 1), BuildingType.TOP);
        Assertions.assertDoesNotThrow(() -> p1.checkConditions(p1.getBuilderList().get(0)));
        Assertions.assertEquals(BUILD, p1.getState());
         /* currently there is no god that can end up in
        a situation where its state is MOVEORBUILD and he can only move. But still */
        Player p3 = new Player("ALE", g, "APOLLO");
        p3.initBuilderList(g.getCell(0, 1));
        g.getCell(0, 2).setBuilder(null);
        TestUtilities.mustSetHeight(g.getCell(0, 3), BuildingType.DOME);
        TestUtilities.mustSetHeight(g.getCell(1, 3), BuildingType.DOME);
        p3.initBuilderList(g.getCell(0, 2));
        p3.setState(MOVEORBUILD);
        Assertions.assertDoesNotThrow(() -> p3.checkConditions(null));
        Assertions.assertEquals(MOVE, p3.getState());
    }

    /**
     * Tests the BUILDORPASS branch in {@link Player#checkConditions(Builder)}
     *
     * @throws Exception aa
     */
    @Test
    void checkConditionsTest2() throws Exception {
        GameTable g = new GameTable(2);
        Player p1 = new Player("Giggino", g, "HEPHAESTUS");
        Player p2 = new Player("Giggino2", g, "DEMETER");
        TestUtilities.mustSetHeight(g.getCell(1, 0), BuildingType.DOME);
        TestUtilities.mustSetHeight(g.getCell(1, 1), BuildingType.DOME);
        TestUtilities.mustSetHeight(g.getCell(1, 2), BuildingType.DOME);
        p1.initBuilderList(g.getCell(0, 0));
        p1.initBuilderList(g.getCell(0, 1));
        p2.initBuilderList(g.getCell(0, 2));
        p2.initBuilderList(g.getCell(0, 3));
        p1.setState(BUILDORPASS);
        /* check that the branch gameTable.nextTurn() gets executed */
        ArrayList<Player> players = new ArrayList<>();
        players.add(p2);
        players.add(p1);
        g.setPlayers(players);
        g.setCurrentBuilder(p1.getBuilderList().get(0));
        Assertions.assertDoesNotThrow(() -> p1.checkConditions(p1.getBuilderList().get(0)));
        assert p1.getState() == WAIT;
        p1.setState(BUILDORPASS);
        g.getCell(0, 0).setBuilder(null);
        TestUtilities.mustSetHeight(g.getCell(0, 0), BuildingType.TOP);
        Assertions.assertDoesNotThrow(() -> p1.checkConditions(null));
        Assertions.assertEquals(BUILDORPASS, p1.getState());
    }

    @Test
    void setConnectionTest() throws Exception {
        SocketClientConnection c = new SocketClientConnection(new Socket(), new Server());
        GameTable g = new GameTable(2);
        Player p1 = new Player("Giggino", g, "HEPHAESTUS");
        p1.setConnection(c);
        assert p1.getConnection() == c;

    }

    @Test
    void constructorTest1() throws Exception {
        Assertions.assertThrows(NullPointerException.class, () -> new Player("ASD", null, new SocketClientConnection(null, null)));
        Assertions.assertThrows(NullPointerException.class, () -> new Player("ASD", null, "ASDASD"));
        GameTable g = new GameTable(2);
        Player p1 = new Player("asd", g, new SocketClientConnection(null, null));
        g.addPlayer(p1);
        Assertions.assertThrows(NickAlreadyTakenException.class, () -> new Player("asd", g, new SocketClientConnection(null, null)));
        Assertions.assertThrows(NickAlreadyTakenException.class, () -> new Player("asd", g, "asdasd"));

    }

    @Test
    void badGodTest1() throws Exception {
        GameTable g = new GameTable(2);
        Field aa = Player.class.getDeclaredField("godClasses");
        aa.setAccessible(true);
        ((Set<Class<?>>) aa.get(null)).add(BadGod.class);
        Field ab = GameTable.class.getDeclaredField("completeGodList");
        ab.setAccessible(true);
        ((List<String>) ab.get(null)).add("TEST_BAD_GOD");
        Player p1 = new Player("asd", g, new SocketClientConnection(null, null));
        p1.setGod(9);
        Assertions.assertThrows(NoSuchMethodException.class, () -> p1.initBuilderList(g.getCell(1, 1)));

    }

    @Test
    void badGodTest2() throws Exception {
        GameTable g = new GameTable(2);
        Field aa = Player.class.getDeclaredField("godClasses");
        aa.setAccessible(true);
        ((Set<Class<?>>) aa.get(null)).add(BadGod2.class);
        Field ab = GameTable.class.getDeclaredField("completeGodList");
        ab.setAccessible(true);
        ((List<String>) ab.get(null)).add("TEST_BAD_GOD_2");
        Player p1 = new Player("asd", g, new SocketClientConnection(null, null));
        p1.setGod(10);
        Assertions.assertThrows(IllegalAccessException.class, () -> p1.initBuilderList(g.getCell(1, 1)));

    }

    @Test
    void badGodTest3() throws Exception {
        GameTable g = new GameTable(2);
        Field aa = Player.class.getDeclaredField("godClasses");
        aa.setAccessible(true);
        ((Set<Class<?>>) aa.get(null)).remove(BadGod2.class);
        Player p1 = new Player("asd", g, new SocketClientConnection(null, null));
        p1.setGod(10);
        Assertions.assertThrows(InvalidBuildException.class, () -> p1.initBuilderList(g.getCell(1, 1)));

    }

}