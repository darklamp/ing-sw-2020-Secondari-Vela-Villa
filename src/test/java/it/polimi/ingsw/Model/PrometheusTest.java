package it.polimi.ingsw.Model;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;
import it.polimi.ingsw.TestUtilities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static it.polimi.ingsw.Client.ClientState.BUILD;
import static it.polimi.ingsw.Client.ClientState.MOVE;

class PrometheusTest {

    @Test
    void isValidBuildTest() throws Exception {
        GameTable g = new GameTable(2);
        Player p1 = new Player("Giggino", g, "PROMETHEUS");
        Player p2 = new Player("Matte", g, "ATLAS");
        ArrayList<Player> players = new ArrayList<>();
        players.add(p2);
        players.add(p1);
        g.setPlayers(players);
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(3, 2);
        Cell c3 = g.getCell(4, 2);
        Builder b1 = new Prometheus(c3, p1);
        Assertions.assertEquals(ClientState.MOVEORBUILD, b1.getFirstState());
        p1.setState(BUILD);
        p2.setState(BUILD);
        Assertions.assertDoesNotThrow(() -> b1.isValidBuild(c1, BuildingType.BASE));
        Assertions.assertThrows(InvalidBuildException.class, () -> c2.setHeight(b1, BuildingType.DOME));

    }

    @Test
    void isValidMoveTest() throws Exception {
        GameTable g = new GameTable(2);

        Player p1 = new Player("Giggino", g, "PROMETHEUS");
        Player p2 = new Player("Matte", g, "ATLAS");
        ArrayList<Player> players = new ArrayList<>();
        players.add(p2);
        players.add(p1);
        g.setPlayers(players);
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(3, 1);
        Cell c3 = g.getCell(3, 2);
        Builder b1 = new Prometheus(c1, p1);
        p1.setState(MOVE);
        Assertions.assertThrows(InvalidMoveException.class, () -> b1.isValidMove(c2));
        p1.setState(MOVE);
        b1.setPosition(c3);
        b1.isValidMove(c2);
        p1.setState(ClientState.MOVE);
        TestUtilities.mustSetHeight(c1, BuildingType.BASE);
        TestUtilities.mustSetHeight(c3, BuildingType.NONE);
        Assertions.assertThrows(InvalidMoveException.class, () -> b1.isValidMove(c1));
        TestUtilities.mustSetHeight(c1, BuildingType.NONE);
        Assertions.assertDoesNotThrow(() -> b1.isValidMove(c1));
    }
}
