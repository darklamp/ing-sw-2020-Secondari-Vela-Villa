package it.polimi.ingsw;

import it.polimi.ingsw.Model.Builder;
import it.polimi.ingsw.Model.Cell;
import it.polimi.ingsw.Model.God;
import it.polimi.ingsw.Model.Player;

@God(name = "TEST_BAD_GOD_2")
public class BadGod2 extends Builder {

    private BadGod2(Cell position, Player player) {
        super(position, player);
    }
}
