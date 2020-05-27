package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.HephaestusException;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

class HephaestusTest {

    @Test
    void isValidBuildTest1() throws Exception {
        GameTable g = new GameTable(2);

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
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            c2.setHeight(b1, BuildingType.DOME);
        });
    }

    @Test
    void isValidBuildTest2() throws Exception {
        GameTable g = new GameTable(2);

        Player p1 = new Player("Giggino", g, "HEPHAESTUS");
        p1.setFirstTime(true);
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        Builder b1 = new Hephaestus(c1, p1);
        c2.mustSetHeight(BuildingType.TOP);
        Assertions.assertDoesNotThrow(() -> {
            c2.setHeight(b1, BuildingType.DOME);
        });
        Field f = Hephaestus.class.getDeclaredField("previous");
        f.setAccessible(true);
        f.set(b1, c1);
        p1.setFirstTime(false);
        c2.mustSetHeight(BuildingType.NONE);
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            c2.setHeight(b1, BuildingType.BASE);
        });
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            c2.setHeight(b1, BuildingType.DOME);
        });
    }

    @Test
    void isValidMoveTest() throws Exception {
        GameTable g = new GameTable(2);

        Player p1 = new Player("Giggino", g, "HEPHAESTUS");
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(3, 1);
        Cell c3 = g.getCell(3, 2);
        Builder b1 = new Hephaestus(c1, p1);

        Assertions.assertThrows(InvalidMoveException.class, () -> {
            b1.isValidMove(c2);
        });
        b1.setPosition(c3);
        b1.isValidMove(c2);
    }
}

