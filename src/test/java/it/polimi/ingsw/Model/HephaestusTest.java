package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HephaestusTest {

    @Test
    void isValidBuildTest() throws Exception{
        GameTable g = new GameTable();
        Player p1 = new Player("Giggino",g);
        Cell c1 = g.getCell(4,3);
        Cell c2 = g.getCell(4,4);
        Builder b1 = new Hephaestus(c1,p1);
        //maybe for hephaestus exception it's better to pass also the cell where I'm building,isn't it?
        Assertions.assertThrows(HephaestusException.class, () -> {
            b1.isValidBuild(c2,BuildingType.BASE);
        })
        Assertions.assertThrows(HephaestusException.class, () -> {
            b1.isValidBuild(c2,BuildingType.MIDDLE);
        })
        Assertions.assertThrows(HephaestusException.class, () -> {
            b1.isValidBuild(c2,BuildingType.TOP);
        })
        c2.setHeight(b1,BuildingType.DOME);
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            b1.isValidBuild(c2,BuildingType.DOME);
        });
        Cell c3 = g.getCell(3,3);
        c3.setHeight(b1,BuildingType.TOP);
        b1.isValidBuild(c3, BuildingType.DOME); //doesn't throw anything beacuse if I've built a dome,I can't build anything over it and it's ok.
    }
}

