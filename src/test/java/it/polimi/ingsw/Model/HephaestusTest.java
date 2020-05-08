package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.HephaestusException;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HephaestusTest {

    @Test
    void isValidBuildTest() throws Exception {
        GameTable g = GameTable.getDebugInstance(2);
        g.setDebugInstance();
        Player p1 = new Player("Giggino", g, "HEPHAESTUS");
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        Builder b1 = new Hephaestus(c1, p1);
        Assertions.assertThrows(HephaestusException.class, () -> {
            c2.setHeight(b1, BuildingType.BASE);
        });
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            c2.setHeight(b1, BuildingType.DOME);
        });
        c2.setHeight(b1, BuildingType.MIDDLE); // no exception
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            c2.setHeight(b1, BuildingType.DOME);
        });
    }
}

