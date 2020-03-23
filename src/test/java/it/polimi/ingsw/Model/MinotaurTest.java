package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.AtlasException;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;
import it.polimi.ingsw.Model.Exceptions.MinotaurException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MinotaurTest {

    @Test
    void isValidMoveTest() throws Exception {
        GameTable g = new GameTable();
        Player p1 = new Player("Giggino",g);
        Player p2 = new Player("Giggino2",g);
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