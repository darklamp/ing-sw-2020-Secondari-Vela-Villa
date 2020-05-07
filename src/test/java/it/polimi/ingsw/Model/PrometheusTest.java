package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PrometheusTest {

    @Test
    void isValidMoveTest() throws Exception {
        GameTable g = GameTable.getDebugInstance(2); g.setDebugInstance();
        Player p1 = new Player("Giggino", g, "PROMETEUS");
        Player p2 = new Player("Matte", g, "PROMETEUS");
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(3, 2);
        Cell c3 = g.getCell(4, 2);
        Cell c4 = g.getCell(3, 3);
        Cell c5 = g.getCell(2, 3);
        Builder b1 = new Prometheus(c3, p1);
            Assertions.assertThrows(PrometheusException.class, () -> {
                c1.mustSetHeight(BuildingType.BASE);
                b1.setPosition(c1);
            });

        Assertions.assertThrows(InvalidBuildException.class, () -> {
            c2.setHeight(b1,BuildingType.DOME);
        });

    }
}
