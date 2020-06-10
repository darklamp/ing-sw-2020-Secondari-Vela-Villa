package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.AtlasException;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;
import it.polimi.ingsw.TestUtilities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AtlasTest {

    /**
     * tests that the the atlas logic works by building a dome on an empty cell and then verifies that I can't build on the now occupied cell anymore
     * @throws Exception --> there's something wrong (that is, if the test does fine no exception are thrown)
     */
    @Test
    void isValidBuildTest() throws Exception{
        GameTable g = new GameTable(2);
        Player p1 = new Player("Giggino", g, "ATLAS");
        Cell c1 = g.getCell(4,3);
        Cell c2 = g.getCell(4, 4);
        Builder b1 = new Atlas(c1, p1);

        Assertions.assertThrows(AtlasException.class, () -> b1.isValidBuild(c2, BuildingType.DOME));
        c2.setHeight(b1, BuildingType.DOME);
        Assertions.assertThrows(InvalidBuildException.class, () -> b1.isValidBuild(c2, BuildingType.DOME));
        Cell c3 = g.getCell(3, 3);
        Assertions.assertThrows(AtlasException.class, () -> b1.isValidBuild(c3, BuildingType.DOME));
        TestUtilities.mustSetHeight(c3, BuildingType.DOME);
        Assertions.assertThrows(InvalidBuildException.class, () -> b1.isValidBuild(c3, BuildingType.DOME));
        Cell c4 = g.getCell(4, 2);
        Assertions.assertDoesNotThrow(() -> b1.isValidBuild(c4, BuildingType.BASE));
        TestUtilities.mustSetHeight(c3, BuildingType.BASE);
        Assertions.assertDoesNotThrow(() -> b1.isValidBuild(c3, BuildingType.MIDDLE));
    }

    @Test
    void isValidMoveTest() throws Exception {
        GameTable g = new GameTable(2);

        Player p1 = new Player("Giggino", g, "ATLAS");
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(3, 1);
        Cell c3 = g.getCell(3, 2);
        Builder b1 = new Atlas(c1, p1);

        Assertions.assertThrows(InvalidMoveException.class, () -> {
            b1.isValidMove(c2);
        });
        b1.setPosition(c3);
        b1.isValidMove(c2);
    }

}