package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PrometeusTest {

    @Test
    void isValidMoveTest() throws Exception {
        GameTable g = GameTable.getDebugInstance(2);
        Player p1 = new Player("Giggino", g);
        Player p2 = new Player("Matte", g);
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(3, 2);
        Cell c3 = g.getCell(4, 2);
        Cell c4 = g.getCell(3, 3);
        Cell c5 = g.getCell(2, 3);
        Builder b1 = new Prometeus(c3, p1);
        if((c4.getHeight().compareTo(c3.getHeight()) <= 0)) {
            Assertions.assertThrows(PrometeusException.class, () -> {
                c1.setHeight(b1, BuildingType.BASE);
                b1.setPosition(c4);
                c5.setHeight(b1, BuildingType.BASE);

            });
        }
        else {
            b1.setPosition(c4);
            c5.setHeight(b1, BuildingType.BASE);
        }
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            c2.setHeight(b1,BuildingType.DOME);
        });

    }
}
