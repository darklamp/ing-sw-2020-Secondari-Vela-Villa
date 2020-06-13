package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.WinnerException;
import it.polimi.ingsw.TestUtilities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static it.polimi.ingsw.Client.ClientState.MOVE;

public class PanTest {
    @Test
    void isValidMoveTest() throws Exception {
        GameTable g = new GameTable(2);

        Player p1 = new Player("Giggino", g, "PAN");
        Player p2 = new Player("Matte", g, "ATLAS");
        ArrayList<Player> players = new ArrayList<>();
        players.add(p2);
        players.add(p1);
        g.setPlayers(players);
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        TestUtilities.mustSetHeight(c1, BuildingType.MIDDLE);
        Builder b1 = new Pan(c1, p1);
        p1.setState(MOVE);
        Assertions.assertThrows(WinnerException.class, () -> b1.setPosition(c2));
        TestUtilities.mustSetHeight(c1, BuildingType.BASE);
        Assertions.assertDoesNotThrow(() -> b1.setPosition(c2));
    }

    @Test
    void isValidBuildTest() throws Exception {
        GameTable g = new GameTable(2);

        Player p1 = new Player("Giggino", g, "PAN");
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        Builder b1 = new Pan(c1, p1);

        Assertions.assertThrows(InvalidBuildException.class, () -> b1.isValidBuild(c2, BuildingType.DOME));
        b1.isValidBuild(c2, BuildingType.BASE);
        Assertions.assertThrows(InvalidBuildException.class, () -> b1.isValidBuild(c2, BuildingType.DOME));
    }
}
