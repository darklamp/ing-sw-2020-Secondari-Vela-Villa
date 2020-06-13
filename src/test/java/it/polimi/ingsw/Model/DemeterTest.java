package it.polimi.ingsw.Model;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        p1.initBuilderList(c1);
        p1.initBuilderList(c5);
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
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(3, 1);
        Cell c3 = g.getCell(3, 2);
        Cell c4 = g.getCell(3, 4);
        Builder b1 = new Demeter(c1, p1);

        Assertions.assertThrows(InvalidMoveException.class, () -> b1.isValidMove(c2));
        p1.setState(ClientState.MOVE);
        b1.isValidMove(c3);
        b1.isValidMove(c4);
    }
}