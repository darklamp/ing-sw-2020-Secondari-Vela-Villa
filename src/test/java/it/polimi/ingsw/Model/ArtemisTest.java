package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.ArtemisException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;
import it.polimi.ingsw.Model.Exceptions.MinotaurException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArtemisTest {

    @Test
    void isValidMoveTest() throws Exception {
        GameTable g = new GameTable();
        Player p1 = new Player("Giggino",g);
        Player p2 = new Player("Giggino2",g);
        Cell c0 = g.getCell(4,2);
        Cell c1 = g.getCell(4,3);
        Cell c2 = g.getCell(4,4);
        Builder b1 = new Artemis(c0,p1);
        Builder b2 = new Atlas(c2,p2);
        Assertions.assertThrows(ArtemisException.class, () -> {
            b1.setPosition(c1);
        });
        Assertions.assertThrows(InvalidMoveException.class, () -> {
            b1.setPosition(c0);
        });
        Assertions.assertThrows(InvalidMoveException.class, () -> {
            b1.setPosition(c2);
        });
        Cell c3 = g.getCell(3,3);
        b1.setPosition(c3); // shouldn't throw
    }
}