package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.PanException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PanTest {
    @Test
    void isValidMoveTest() throws Exception {
        GameTable g = new GameTable(2);

        Player p1 = new Player("Giggino", g, "PAN");
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        c1.mustSetHeight(BuildingType.MIDDLE);
        Builder b1 = new Pan(c1, p1);
        Assertions.assertThrows(PanException.class, () -> {
            b1.setPosition(c2);
        });
    }

    @Test
    void isValidBuildTest() throws Exception {
        GameTable g = new GameTable(2);

        Player p1 = new Player("Giggino", g, "PAN");
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        Builder b1 = new Pan(c1, p1);

        Assertions.assertThrows(InvalidBuildException.class, () -> {
            b1.isValidBuild(c2, BuildingType.DOME);
        });
        b1.isValidBuild(c2, BuildingType.BASE);
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            b1.isValidBuild(c2, BuildingType.DOME);
        });
    }
}
