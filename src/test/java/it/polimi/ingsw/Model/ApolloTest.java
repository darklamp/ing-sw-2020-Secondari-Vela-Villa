package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.HephaestusException;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;

class ApolloTest {

    @Test
    void isValidMoveTest() throws Exception{
        GameTable g = new GameTable();
        Player p1 = new Player("Giggino",g);
        Player p2 = new Player("Pippo",g);
        Cell c1 = g.getCell(4,3);
        Cell c2 = g.getCell(4,4);
        Cell c3 = g.getCell(4,2);
        Builder b1 = new Apollo(c1,p1);
        Builder b2 = new Apollo(c2,p2);

        Assertions.assertThrows(ApolloException.class, () -> {
            b1.setPosition(c2);
        });
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            c3.setHeight(b1,BuildingType.DOME);
        });
        c3.setHeight(b1,BuildingType.MIDDLE); // no exception
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            c3.setHeight(b1,BuildingType.DOME);
        });
    }
}