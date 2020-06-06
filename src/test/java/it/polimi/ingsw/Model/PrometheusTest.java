package it.polimi.ingsw.Model;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;
import it.polimi.ingsw.Model.Exceptions.PrometheusException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

class PrometheusTest {

    @Test
    void isValidBuildTest() throws Exception {
        Field a = Player.class.getDeclaredField("turnState");
        a.setAccessible(true);
        GameTable g = new GameTable(2);

        Player p1 = new Player("Giggino", g, "PROMETEUS");
        Player p2 = new Player("Matte", g, "PROMETEUS");
        a.set(p1, ClientState.MOVEORBUILD);
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(3, 2);
        Cell c3 = g.getCell(4, 2);
        Builder b1 = new Prometheus(c3, p1);
        Assertions.assertEquals(ClientState.MOVEORBUILD, b1.getFirstState());

        Assertions.assertThrows(PrometheusException.class, () -> c1.setHeight(b1, BuildingType.BASE));
        Assertions.assertThrows(InvalidBuildException.class, () -> c2.setHeight(b1, BuildingType.DOME));

    }

    @Test
    void isValidMoveTest() throws Exception {
        GameTable g = new GameTable(2);

        Player p1 = new Player("Giggino", g, "PROMETHEUS");
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(3, 1);
        Cell c3 = g.getCell(3, 2);
        Builder b1 = new Prometheus(c1, p1);

        Assertions.assertThrows(InvalidMoveException.class, () -> {
            b1.isValidMove(c2);
        });
        b1.setPosition(c3);
        b1.isValidMove(c2);
        p1.setState(ClientState.MOVE);
        c1.mustSetHeight(BuildingType.BASE);
        c3.mustSetHeight(BuildingType.NONE);
        Assertions.assertThrows(InvalidMoveException.class, () -> b1.isValidMove(c1));
        c1.mustSetHeight(BuildingType.NONE);
        Assertions.assertDoesNotThrow(() -> b1.isValidMove(c1));
    }
}
