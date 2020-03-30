package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DemeterTest {

    @Test
    void isValidBuildTest() throws Exception {
        GameTable g = GameTable.getDebugInstance(2); g.setDebugInstance();
        Player p1 = new Player("Giggino",g);
        Cell c1 = g.getCell(4,3);
        Cell c2 = g.getCell(4,4);
        Builder b1 = new Demeter(c1,p1);
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            //b1.isValidBuild(c2,BuildingType.DOME);
            c2.setHeight(b1, BuildingType.DOME);
        });
        Assertions.assertThrows(DemeterException.class, () -> {
            //b1.isValidBuild(c2,BuildingType.BASE);
            c2.setHeight(b1, BuildingType.BASE);
        });
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            c2.setHeight(b1, BuildingType.MIDDLE);
           // b1.isValidBuild(c2,BuildingType.MIDDLE);
        });
        Cell c3 = g.getCell(3,3);
        c3.setHeight(b1, BuildingType.BASE); // shouldn't throw exceptions, since I'm building on a different cell than before
        Cell c4 = g.getCell(1,1);
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            c4.setHeight(b1, BuildingType.BASE);
            // b1.isValidBuild(c2,BuildingType.MIDDLE);
        });
      /*
        Assertions.assertThrows(AtlasException.class, () -> {
            b1.isValidBuild(c3,BuildingType.DOME);
        });
        Cell c4 = g.getCell(4,2);
        b1.isValidBuild(c4, BuildingType.BASE);
    }*/
    }
}