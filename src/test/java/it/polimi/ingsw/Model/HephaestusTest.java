package it.polimi.ingsw.Model;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.Exceptions.HephaestusException;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;
import it.polimi.ingsw.TestUtilities;
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
        p1.setFirstTime(true);
        TestUtilities.mustSetHeight(c2, BuildingType.TOP);
        Assertions.assertDoesNotThrow(() -> c2.setHeight(b1, BuildingType.DOME));
        p1.setFirstTime(true);
        TestUtilities.mustSetHeight(c2, BuildingType.MIDDLE);
        Assertions.assertDoesNotThrow(() -> c2.setHeight(b1, BuildingType.TOP));
        TestUtilities.mustSetHeight(c2, BuildingType.BASE);

        Assertions.assertThrows(InvalidBuildException.class, () -> c2.setHeight(b1, BuildingType.DOME));
        Assertions.assertThrows(InvalidBuildException.class, () -> c2.setHeight(b1, BuildingType.DOME));
    }

    @Test
    void isValidBuildTest2() throws Exception {
        GameTable g = new GameTable(2);

        Player p1 = new Player("Giggino", g, "HEPHAESTUS");
        p1.setFirstTime(true);
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        Cell c3 = g.getCell(4, 2);
        Builder b1 = new Hephaestus(c1, p1);
        TestUtilities.mustSetHeight(c2, BuildingType.TOP);
        Assertions.assertDoesNotThrow(() -> {
            c2.setHeight(b1, BuildingType.DOME);
        });
        Field f = Hephaestus.class.getDeclaredField("previous");
        f.setAccessible(true);
        f.set(b1, c1);
        p1.setFirstTime(false);
        TestUtilities.mustSetHeight(c2, BuildingType.NONE);
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            c2.setHeight(b1, BuildingType.BASE);
        });
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            c2.setHeight(b1, BuildingType.DOME);
        });
        p1.setState(ClientState.BUILD);
        f.set(b1, c3);
        Assertions.assertDoesNotThrow(() -> b1.isValidBuild(c3, BuildingType.BASE));
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

