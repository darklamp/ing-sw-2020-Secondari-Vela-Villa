package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.AtlasException;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.NickAlreadyTakenException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AtlasTest {

    @Test
    void isValidBuildTest() throws InvalidBuildException, AtlasException, NickAlreadyTakenException {
        GameTable g = new GameTable();
        Player p1 = new Player("Giggino",g);
        Cell c1 = new Cell(4,3);
        Cell c2 = new Cell(4,4);
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