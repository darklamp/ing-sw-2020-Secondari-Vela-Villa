package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.ApolloException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PrometeusTest {

    @Test
    void isValidMoveTest() throws Exception {
        GameTable g = GameTable.getDebugInstance(2); g.setDebugInstance();
        Player p1 = new Player("Giggino", g);
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        Cell c3 = g.getCell(4, 0);
        Builder b1 = new Prometeus(c1, p1);
        //TODO
    }
}
