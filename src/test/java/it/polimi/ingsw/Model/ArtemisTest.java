package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static it.polimi.ingsw.Client.ClientState.*;

class ArtemisTest {

    @Test
    void isValidMoveTest() throws Exception {
        GameTable g = new GameTable(2);
        Player p1 = new Player("Giggino", g, "ARTEMIS");
        Player p2 = new Player("Giggino2", g, "ATLAS");
        Cell c0 = g.getCell(4, 2);
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        Builder b1 = new Artemis(c0, p1);
        ArrayList<Player> players = new ArrayList<>();
        players.add(p2);
        players.add(p1);
        g.setPlayers(players);
        p1.setState(MOVE);
        b1.setPosition(c1);
        Assertions.assertEquals(c1, b1.getPosition());
        Assertions.assertEquals(MOVEORBUILD, b1.getPlayer().getState());
        Assertions.assertThrows(InvalidMoveException.class, () -> b1.setPosition(c0));
        b1.setPosition(c2);
        Assertions.assertEquals(c2, b1.getPosition());
        Assertions.assertEquals(BUILD, b1.getPlayer().getState());
        Cell c3 = g.getCell(3, 3);
        Assertions.assertThrows(InvalidMoveException.class, () -> b1.setPosition(c3));
    }

    @Test
    void isValidBuildTest() throws Exception {
        GameTable g = new GameTable(2);

        Player p1 = new Player("Giggino", g, "ARTEMIS");
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        Builder b1 = new Artemis(c1, p1);

        Assertions.assertThrows(InvalidBuildException.class, () -> {
            b1.isValidBuild(c2, BuildingType.DOME);
        });
        b1.isValidBuild(c2, BuildingType.BASE);
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            b1.isValidBuild(c2, BuildingType.DOME);
        });
    }
}