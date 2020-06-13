package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.TestUtilities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static it.polimi.ingsw.Client.ClientState.MOVE;

public class AthenaTest {
    /**
     * This test first checks that the default gametable has athenaMove set to false; it then
     * proceeds to build on a cell and move on that cell with an Athena builder.
     * This is expected to trigger {@link GameTable#setAthenaMove(boolean)}, setting athenaMove to true.
     * The value is then set back to false with another call to the same method if, during the next turn, Athena
     * doesn't go up a level. This is also checked by this test.
     *
     * @throws Exception should not
     */
    @Test
    void isValidBuildTest() throws Exception {
        GameTable g = new GameTable(2);

        Player p1 = new Player("Giggino", g, "ATHENA");
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        Builder b1 = new Athena(c1, p1);

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
        Player p1 = new Player("Giggino", g, "ATHENA");
        Player p2 = new Player("Giggino2", g, "ATLAS");
        ArrayList<Player> players = new ArrayList<>();
        players.add(p2);
        players.add(p1);
        g.setPlayers(players);
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        Cell c3 = g.getCell(3, 4);
        Builder b1 = new Athena(c1, p1);
        Assertions.assertFalse(g.getAthenaMove());
        TestUtilities.mustSetHeight(c2, BuildingType.BASE);
        p1.setState(MOVE);
        b1.setPosition(c2);
        Assertions.assertTrue(g.getAthenaMove());
        p1.setState(MOVE);
        b1.setPosition(c3);
        Assertions.assertFalse(g.getAthenaMove());
    }
}
