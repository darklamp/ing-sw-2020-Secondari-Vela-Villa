package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;
import it.polimi.ingsw.Model.Exceptions.MinotaurException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MinotaurTest {

    @Test
    void isValidBuildTest() throws Exception {
        GameTable g = new GameTable(2);

        Player p1 = new Player("Giggino", g, "MINOTAUR");
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        Builder b1 = new Minotaur(c1, p1);

        Assertions.assertThrows(InvalidBuildException.class, () -> {
            b1.isValidBuild(c2, BuildingType.DOME);
        });
        b1.isValidBuild(c2, BuildingType.BASE);
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            b1.isValidBuild(c2, BuildingType.DOME);
        });
    }

    @Test
    void isValidMoveTest() throws Exception {
        GameTable g = new GameTable(2);
        Player p1 = new Player("Giggino", g, "MINOTAUR");
        Player p2 = new Player("Giggino2", g, "ATLAS");
        Cell c0 = g.getCell(4, 2);
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        Builder b1 = new Minotaur(c0, p1);
        Builder b2 = new Atlas(c1, p2);
        Assertions.assertThrows(MinotaurException.class, () -> {
            b1.isValidMove(c1);
        });
        Assertions.assertThrows(InvalidMoveException.class, () -> {
            b1.isValidMove(c2);
        });
    }

    @Test
    void checkEmptyCellBehindTest() throws Exception {
        GameTable g = new GameTable(2);
        Player p1 = new Player("Giggino", g, "MINOTAUR");
        Player p2 = new Player("Giggino2", g, "ATLAS");
        Cell c0 = g.getCell(4, 2);
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        Cell c3 = g.getCell(3, 2);
        Cell c4 = g.getCell(2, 2);
        Cell c5 = g.getCell(3, 1);
        Cell c6 = g.getCell(2, 0);
        Builder b1 = new Minotaur(c0, p1);
        Builder b2 = new Atlas(c2, p2);
        Assertions.assertFalse(((Minotaur) b1).checkEmptyCellBehind(c1));
        Assertions.assertTrue(((Minotaur) b1).checkEmptyCellBehind(c3));
        Assertions.assertTrue(((Minotaur) b1).checkEmptyCellBehind(c5));


    }

    @Test
    void getCellBehindTest() throws Exception {
        GameTable g = new GameTable(2);
        Player p1 = new Player("Giggino", g, "MINOTAUR");
        Player p2 = new Player("Giggino2", g, "ATLAS");
        Cell c0 = g.getCell(4, 2);
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        Cell c3 = g.getCell(3, 2);
        Cell c4 = g.getCell(2, 2);
        Cell c5 = g.getCell(3, 1);
        Cell c6 = g.getCell(2, 0);
        Builder b1 = new Minotaur(c0, p1);
        Builder b2 = new Atlas(c2, p2);
        Assertions.assertEquals(((Minotaur) b1).getCellBehind(c1).getFirst(), 0);
        Assertions.assertEquals(((Minotaur) b1).getCellBehind(c1).getSecond(), 1);
        Assertions.assertEquals(((Minotaur) b1).getCellBehind(c3).getFirst(), -1);
        Assertions.assertEquals(((Minotaur) b1).getCellBehind(c3).getSecond(), 0);



    }
}