package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.PanException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PanTest {
    @Test
    void isValidMoveTest() throws Exception {
        GameTable g = GameTable.getDebugInstance(2); g.setDebugInstance();
        Player p1 = new Player("Giggino", g, "PAN");
        Cell c1 = g.getCell(4,3);
        Cell c2 = g.getCell(4,4);
        c1.mustSetHeight(BuildingType.MIDDLE);
        Builder b1 = new Pan(c1,p1);
        Assertions.assertThrows(PanException.class, () -> {
            b1.setPosition(c2);
        });
    }
}
