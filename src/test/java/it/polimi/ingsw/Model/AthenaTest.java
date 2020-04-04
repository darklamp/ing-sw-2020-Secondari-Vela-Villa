package it.polimi.ingsw.Model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AthenaTest {
    /**
     * This test first checks that the default gametable has athenaMove set to false; it then
     * proceeds to build on a cell and move on that cell with an Athena builder.
     * This is expected to trigger {@link GameTable#setAthenaMove(boolean)}, setting athenaMove to true.
     * The value is then set back to false with another call to the same method if, during the next turn, Athena
     * doesn't go up a level. This is also checked by this test.
     * @throws Exception should not
     */
    @Test
    void isValidMoveTest() throws Exception {
        GameTable g = GameTable.getDebugInstance(2); g.setDebugInstance();
        Player p1 = new Player("Giggino", g, "ATHENA");
        Cell c1 = g.getCell(4,3);
        Cell c2 = g.getCell(4,4);
        Cell c3 = g.getCell(3,4);
        Builder b1 = new Athena(c1,p1);
        Assertions.assertFalse(g.getAthenaMove());
        c2.mustSetHeight(BuildingType.BASE);
        b1.setPosition(c2);
        Assertions.assertTrue(g.getAthenaMove());
        b1.setPosition(c3);
        Assertions.assertFalse(g.getAthenaMove());
    }
}
