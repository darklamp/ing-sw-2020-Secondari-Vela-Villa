package it.polimi.ingsw.Model;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;
import it.polimi.ingsw.TestUtilities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;

class DemeterTest {

    @Test
    void isValidBuildTest() throws Exception {
        GameTable g = new GameTable(2);
        Player p1 = new Player("Giggino", g, "DEMETER");
        Player p2 = new Player("Giggino2", g, "ATLAS");
        ArrayList<Player> players = new ArrayList<>();
        players.add(p2);
        players.add(p1);
        g.setPlayers(players);
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        Cell c5 = g.getCell(4, 2);
        Cell c22 = g.getCell(0, 0);
        p1.initBuilderList(c1);
        p1.initBuilderList(c5);
        p2.initBuilderList(c22);
        Builder b1 = p1.getBuilderList().get(0);
        Assertions.assertThrows(InvalidBuildException.class, () -> c2.setHeight(b1, BuildingType.DOME));
        p1.setState(ClientState.BUILD);
        Assertions.assertDoesNotThrow(() -> c2.setHeight(b1, BuildingType.BASE));
        Assertions.assertThrows(InvalidBuildException.class, () -> b1.isValidBuild(c2, BuildingType.MIDDLE));
        Cell c4 = g.getCell(1, 1);
        Assertions.assertThrows(InvalidBuildException.class, () -> c4.setHeight(b1, BuildingType.BASE));
    }
    @Test
    void isValidMoveTest() throws Exception {
        GameTable g = new GameTable(2);

        Player p1 = new Player("Giggino", g, "DEMETER");
        Player p2 = new Player("Giggino3", g, "ATLAS");

        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(3, 1);
        Cell c3 = g.getCell(3, 2);
        Cell c4 = g.getCell(3, 4);
        Cell c22 = g.getCell(0, 0);

        p1.initBuilderList(c1);
        p2.initBuilderList(c22);

        Builder b1 = p1.getBuilderList().get(0);

        Assertions.assertThrows(InvalidMoveException.class, () -> b1.isValidMove(c2));
        p1.setState(ClientState.MOVE);
        b1.isValidMove(c3);
        b1.isValidMove(c4);
    }

    @Test
    void clearPreviousTest() throws Exception {
        GameTable g = new GameTable(2);
        Player p1 = new Player("Giggino", g, "DEMETER");
        p1.initBuilderList(g.getCell(1, 2));
        p1.initBuilderList(g.getCell(1, 3));
        assert TestUtilities.getAccessibleField("previous", p1.getBuilderList().get(0)) == null;
        Field f = Demeter.class.getDeclaredField("previous");
        f.setAccessible(true);
        f.set(p1.getBuilderList().get(0), new Cell(1, 1, g));
        p1.getBuilderList().get(0).clearPrevious();
        assert TestUtilities.getAccessibleField("previous", p1.getBuilderList().get(0)) == null;
    }
}