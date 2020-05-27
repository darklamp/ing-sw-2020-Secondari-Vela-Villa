package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.ApolloException;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ApolloTest {

    @Test
    void isValidBuildTest() throws Exception {
        GameTable g = new GameTable(2);

        Player p1 = new Player("Giggino", g, "APOLLO");
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        Builder b1 = new Apollo(c1, p1);

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
        Player p1 = new Player("Giggino", g, "APOLLO");
        Player p2 = new Player("Pippo", g, "APOLLO");
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        Cell c3 = g.getCell(4, 0);
        Cell c4 = g.getCell(4, 2);
        Builder b1 = new Apollo(c1, p1);
        Builder b2 = new Apollo(c2, p2);
        Builder b3 = new Apollo(c4, p1);
        Assertions.assertThrows(ApolloException.class, () -> {
            b1.isValidMove(c2);
        });

        Assertions.assertThrows(InvalidMoveException.class, () -> {
            b1.isValidMove(c4);
        });
        Assertions.assertThrows(InvalidMoveException.class, () -> {
            b1.setPosition(c1);
        });
        Assertions.assertThrows(InvalidMoveException.class, () -> {
            b1.setPosition(c3);
        });
        Cell c0 = g.getCell(3, 3);
        b1.setPosition(c0); // shouldn't throw
    }
}