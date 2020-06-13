package it.polimi.ingsw.Model;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;

import static it.polimi.ingsw.Client.ClientState.WAIT;

public class Hephaestus extends Builder {
    private Cell previous;  //ci salvo la cella dove costruisco la prima volta

    Hephaestus(Cell position, Player player) {
        super(position, player);
    }

    @Override
    protected ClientState isValidBuild(Cell cell, BuildingType newheight) throws InvalidBuildException {
        if (this.getPlayer().isFirstTime()) {
            super.isValidBuild(cell, newheight);
            verifyBuild(cell, newheight);
            this.getPlayer().setFirstTime(false);
            if (!(newheight == BuildingType.DOME || newheight == BuildingType.TOP)) {
                previous = cell;
                return ClientState.BUILDORPASS;
            } else return WAIT;
        } else {
            super.isValidBuild(cell, newheight);
            verifyBuild(cell, newheight);
            if (!cell.equals(previous)) {
                throw new InvalidBuildException();
            }
            return WAIT;
        }
    }

    @Override
    public void isValidMove(Cell finalPoint) throws InvalidMoveException {
        super.isValidMove(finalPoint);
        verifyMove(finalPoint);
    }

    @Override
    protected void clearPrevious() {
        previous = null;
    }
}
