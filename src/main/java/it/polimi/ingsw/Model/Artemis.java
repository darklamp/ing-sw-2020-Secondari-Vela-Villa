package it.polimi.ingsw.Model;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;

import static it.polimi.ingsw.Client.ClientState.WAIT;

@God(name = "ARTEMIS")
public class Artemis extends Builder {
    Artemis(Cell position, Player player) {
        super(position, player);
    }

    /**
     * Cell on which Artemis came from, and to which she cannot move again until next turn.
     */
    private Cell previous = null;

    @Override
    public ClientState isValidBuild(Cell cell, BuildingType newheight) throws InvalidBuildException {
        super.isValidBuild(cell, newheight);
        verifyBuild(cell, newheight);
        return WAIT;
    }

    @Override
    void isValidMove(Cell finalPoint) throws InvalidMoveException {
        boolean firstTime = this.getPlayer().isFirstTime();
        if (firstTime) {
            super.isValidMove(finalPoint);
            verifyMove(finalPoint);
        } else {
            if (finalPoint.equals(previous)) {
                throw new InvalidMoveException();
            } else {
                super.isValidMove(finalPoint);
                verifyMove(finalPoint);
            }
        }
    }

    @Override
    protected ClientState executeMove(Cell position) {
        Cell prevPos = this.getPosition();
        super.executeMove(position);
        if (previous == null) {
            this.getPlayer().setFirstTime(false);
            previous = prevPos;
            return ClientState.MOVEORBUILD;
        } else {
            previous = null;
            return ClientState.BUILD;
        }
    }

    @Override
    protected void clearPrevious() {
        previous = null;
    }

}
