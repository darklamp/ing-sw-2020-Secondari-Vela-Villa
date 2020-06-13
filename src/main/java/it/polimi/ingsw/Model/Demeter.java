package it.polimi.ingsw.Model;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;

import static it.polimi.ingsw.Client.ClientState.WAIT;

public class Demeter extends Builder {
    private Cell previous;

    Demeter(Cell position, Player player) {
        super(position, player);
    }

    @Override
    protected ClientState isValidBuild(Cell cell, BuildingType newheight) throws InvalidBuildException {
        if (this.getPlayer().isFirstTime()) {
            super.isValidBuild(cell, newheight);
            verifyBuild(cell, newheight);
            this.getPlayer().setFirstTime(false);
            previous = cell;
            return ClientState.BUILDORPASS;
        } else {
            if (cell.equals(previous)) {
                throw new InvalidBuildException();
            } else {
                super.isValidBuild(cell, newheight);
                verifyBuild(cell, newheight);
                return WAIT;
            }
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