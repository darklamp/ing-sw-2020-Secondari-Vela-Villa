package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.AtlasException;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;
import it.polimi.ingsw.Model.Exceptions.NickAlreadyTakenException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AtlasTest {

    /**
     * tests that the the atlas logic works by building a dome on an empty cell and then verifies that I can't build on the now occupied cell anymore
     * @throws Exception --> there's something wrong (that is, if the test does fine no exception are thrown)
     */
    @Test
    void isValidBuildTest() throws InvalidBuildException, AtlasException, NickAlreadyTakenException, InvalidCoordinateException {
        GameTable g = new GameTable();
        Player p1 = new Player("Giggino",g);
        Cell c1 = g.getCell(4,3);
        Cell c2 = g.getCell(4,4);
        Builder b1 = new Atlas(c1,p1);
        Assertions.assertThrows(AtlasException.class, () -> {
            b1.isValidBuild(c2,BuildingType.DOME);
        });
        c2.setHeight(b1,BuildingType.DOME);
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            b1.isValidBuild(c2,BuildingType.DOME);
        });
    }

    @Test
    void isValidMove() {
    }
}