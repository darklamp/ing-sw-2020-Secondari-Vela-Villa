package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HephaestusTest {

    @Test
    void isValidBuildTest() throws Exception{
        GameTable g = GameTable.getDebugInstance(2);
        Player p1 = new Player("Giggino",g);
        Cell c1 = g.getCell(4,3);
        Cell c2 = g.getCell(4,4);
        Builder b1 = new Hephaestus(c1,p1);
        //maybe for hephaestus exception it's better to pass also the cell where I'm building,isn't it?
        Assertions.assertThrows(HephaestusException.class, () -> {
            c2.setHeight(b1,BuildingType.BASE);
        });
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            c2.setHeight(b1,BuildingType.DOME);
        });
        c2.setHeight(b1,BuildingType.MIDDLE); // no exception
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            c2.setHeight(b1,BuildingType.DOME);
        });
    }
}

