package it.polimi.ingsw.Model;

import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static it.polimi.ingsw.Model.Cell.movableCell;
import static org.junit.jupiter.api.Assertions.*;

class CellTest {
    static GameTable g;
    static Cell c1,c2;
    static Player p1;
    static Player p2;
    static Builder b1;
    static Builder b2;

    /**
     * Initiates various support variables.
     */
    @BeforeAll
    static void init() throws Exception{
        g = GameTable.getDebugInstance(2); g.setDebugInstance();
        c1 = g.getCell(4,4);
        c2 = g.getCell(2,0);
        p1 = new Player("Giggino",g);
        p2 = new Player("Giggino2",g);
        b1 = new Minotaur(c1,p1);
        b2 = new Atlas(c2,p2);
    }

    /**
     * Tests the getNear method by filling "test[n]" lists of Cell objects and
     * comparing them to the item returned by getNear.
     * For example, the first test item (item1) is filled with the cells (4,3) , (3,3) , (3,4) only,
     * because they are known to be the only cells near c1 (4,4). The assertion tests that
     * every single one of those cells is contained in the object returned by getNear and
     * that same object contains only those values (this is done by checking the item's size).
     */
    @Test
    void getNearTest() throws Exception{
        ArrayList<Cell> test1 = new ArrayList<>();
        test1.add(g.getCell(4,3));
        test1.add(g.getCell(3,3));
        test1.add(g.getCell(3,4));
        assertTrue(test1.containsAll(c1.getNear()));
        assertEquals(test1.size(),3);
        ArrayList<Cell> test2 = new ArrayList<>();
        test2.add(g.getCell(1,0));
        test2.add(g.getCell(1,1));
        test2.add(g.getCell(2,1));
        test2.add(g.getCell(3,1));
        test2.add(g.getCell(3,0));
        assertTrue(test2.containsAll(c2.getNear()));
        assertEquals(test2.size(),5);
    }

    /**
     * Tests the movableCell method by building on top of three cells.
     * Two of the cells are set to a BASE level, whereas the last one is set to DOME level.
     * The test expects the function to return true when checking the two BASE cells,
     * which have no builders on them;
     * it expects the function to return false when tested with the DOME cell, and when tested
     * with the cells on which the two builders (p1 and p2) are located.
     */
    @Test
    void movableCellTest() throws Exception{
        GameTable.getCell(4,3).setHeight(b1,BuildingType.BASE);
        GameTable.getCell(3,3).setHeight(b1,BuildingType.BASE);
        GameTable.getCell(3,4).setHeight(b1,BuildingType.BASE);
        GameTable.getCell(3,4).setHeight(b1,BuildingType.MIDDLE);
        GameTable.getCell(3,4).setHeight(b1,BuildingType.TOP);
        GameTable.getCell(3,4).setHeight(b1,BuildingType.DOME);

        assertTrue(movableCell(4,3));
        assertTrue(movableCell(3,3));
        assertFalse(movableCell(3,4));
        assertFalse(movableCell(4,4));
        assertFalse(movableCell(2,0));
    }

}
