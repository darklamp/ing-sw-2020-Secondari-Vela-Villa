package it.polimi.ingsw.Model;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.PrometheusException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

class PrometheusTest {

    @Test
    void isValidMoveTest() throws Exception {
        Field a = Player.class.getDeclaredField("turnState");
        a.setAccessible(true);
        GameTable g = GameTable.getDebugInstance(2);
        g.setDebugInstance();
        Player p1 = new Player("Giggino", g, "PROMETEUS");
        Player p2 = new Player("Matte", g, "PROMETEUS");
        a.set(p1, ClientState.MOVEORBUILD);
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(3, 2);
        Cell c3 = g.getCell(4, 2);
        Builder b1 = new Prometheus(c3, p1);
        Assertions.assertThrows(PrometheusException.class, () -> c1.setHeight(b1, BuildingType.BASE));
        Assertions.assertThrows(InvalidBuildException.class, () -> c2.setHeight(b1, BuildingType.DOME));

    }
}
