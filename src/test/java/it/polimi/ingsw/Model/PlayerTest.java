package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.MinotaurException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class  PlayerTest {

    @Test
    void initBuilderList() throws Exception {
        GameTable g = GameTable.getDebugInstance(2); g.setDebugInstance();
        Player p1 = new Player("Giggino", g, "MINOTAUR");
        Player p2 = new Player("Giggino2", g, "ATLAS");
        Cell c1 = g.getCell(4,3);
        Cell c2 = g.getCell(4,4);
        Cell c3 = g.getCell(4,1);
        Cell c4 = g.getCell(4,2);
        Builder b1 = new Minotaur(c1,p1);
        p2.initBuilderList(c2);
        p2.initBuilderList(c3);

        Assertions.assertThrows(InvalidBuildException.class, () -> {
            p1.initBuilderList(c2);  //parte l'eccezione perchè la cella c2 è occupata dal player p2
        });
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            p1.initBuilderList(c1);  //parte l'eccezione perchè la cella c1 è occupata dal suo primo builder b1
        });
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            p2.initBuilderList(c4); // anche se è libera la cella parte l'eccezione perchè p2 ha già due builder posizionati
        });
    }

}