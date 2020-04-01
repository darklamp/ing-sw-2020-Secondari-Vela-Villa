package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;
import it.polimi.ingsw.Model.Exceptions.MinotaurException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MinotaurTest {

    @Test
    void isValidMoveTest() throws Exception {
        GameTable g = GameTable.getDebugInstance(2); g.setDebugInstance();
        Player p1 = new Player("Giggino", g, "MINOTAUR");
        Player p2 = new Player("Giggino2", g, "ATLAS");
        Cell c0 = g.getCell(4,2);
        Cell c1 = g.getCell(4,3);
        Cell c2 = g.getCell(4,4);
        Builder b1 = new Minotaur(c0,p1);
        Builder b2 = new Atlas(c1,p2);
        Assertions.assertThrows(MinotaurException.class, () -> {
            b1.isValidMove(c1);
        });
        Assertions.assertThrows(InvalidMoveException.class, () -> {
            b1.isValidMove(c2);
        });
    }

}