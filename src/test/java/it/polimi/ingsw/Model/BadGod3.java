package it.polimi.ingsw.Model;

@God(name = "TEST_BAD_GOD_3")
public class BadGod3 extends Builder {
    public BadGod3(Cell position, Player player) {
        super(position, player);
    }

    @Override
    protected boolean buildHandicap(Cell cell, BuildingType height) {
        return true;
    }
}
