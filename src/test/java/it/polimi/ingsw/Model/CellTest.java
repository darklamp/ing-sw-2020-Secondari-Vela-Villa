package it.polimi.ingsw.Model;

import it.polimi.ingsw.Network.SocketClientConnection;
import it.polimi.ingsw.Utility.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static it.polimi.ingsw.Model.BuildingType.BASE;
import static it.polimi.ingsw.Model.BuildingType.NONE;
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
        g = new GameTable(2);
        c1 = g.getCell(4, 4);
        c2 = g.getCell(2, 0);
        p1 = new Player("Giggino", g, "MINOTAUR");
        p2 = new Player("Giggino2", g, "ATLAS");
        b1 = new Minotaur(c1, p1);
        b2 = new Atlas(c2, p2);
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
        g.getCell(4,3).setHeight(b1,BuildingType.BASE);
        g.getCell(3,3).setHeight(b1,BuildingType.BASE);
        g.getCell(3,4).setHeight(b1,BuildingType.BASE);
        g.getCell(3,4).setHeight(b1,BuildingType.MIDDLE);
        g.getCell(3, 4).setHeight(b1, BuildingType.TOP);
        g.getCell(3, 4).setHeight(b1, BuildingType.DOME);

        assertTrue(c1.movableCell(4, 3));
        assertTrue(c1.movableCell(3, 3));
        assertFalse(c1.movableCell(3, 4));
        assertFalse(c1.movableCell(4, 4));
        assertFalse(c1.movableCell(2, 0));
    }

    @Test
    void mustSetHeight() {
        GameTable g = new GameTable(2);
        Cell c = new Cell(4, 4, g);
        c.mustSetHeight(BASE);
        assertEquals(c.getHeight(), BASE);
    }

    @Test
    void setHeight() throws Exception {
        GameTable g = new GameTable(2);
        Cell c = g.getCell(1, 1);
        Cell c1 = g.getCell(1, 2);

        Builder b = new Prometheus(c1, new Player("g", g, (SocketClientConnection) null));
        c.setHeight(b, BASE);
        assertEquals(c.getHeight(), BASE);
    }

    @Test
    void getNear() throws Exception {
        GameTable g = new GameTable(2);
        Cell c = new Cell(4, 4, g);
        ArrayList<Cell> b = c.getNear();

        assertTrue(b.contains(g.getCell(3, 4)));
        assertTrue(b.contains(g.getCell(3, 3)));
        assertTrue(b.contains(g.getCell(4, 3)));
        assertEquals(3, b.size());
    }

    @Test
    void movableCell() {
    }

    @Test
    void setBuilder() throws Exception {
        GameTable g = new GameTable(2);
        Cell c = new Cell(4, 4, g);
        Builder b = new Prometheus(c, new Player("g", g, (SocketClientConnection) null));
        c.setBuilder(b);
        assertEquals(c.getBuilder(), b);
    }

    @Test
    void getBuilder() {
        Cell c = new Cell(4, 4, new GameTable(2));
        assertNull(c.getBuilder());
    }

    @Test
    void getRow() {
        Cell c = new Cell(4, 4, new GameTable(2));
        assertEquals(4, c.getRow());
    }

    @Test
    void getColumn() {
        Cell c = new Cell(4, 4, new GameTable(2));
        assertEquals(4, c.getColumn());
    }

    @Test
    void getPosition() {
        Cell c = new Cell(4, 4, new GameTable(2));
        assertEquals(new Pair(4, 4), c.getPosition());
    }

    @Test
    void getHeight() {
        Cell c = new Cell(4, 4, new GameTable(2));
        assertEquals(NONE, c.getHeight());
    }
}
