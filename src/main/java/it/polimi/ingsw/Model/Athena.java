package it.polimi.ingsw.Model;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;

import static it.polimi.ingsw.Client.ClientState.BUILD;
import static it.polimi.ingsw.Client.ClientState.WAIT;

public class Athena extends Builder {
    Athena(Cell position, Player player) {
        super(position, player);
    }

    @Override
    public ClientState isValidBuild(Cell cell, BuildingType newheight) throws InvalidBuildException {
        super.isValidBuild(cell, newheight);
        verifyBuild(cell, newheight);
        return WAIT;
    }

    @Override
    void isValidMove(Cell finalPoint) throws InvalidMoveException {
        super.isValidMove(finalPoint);
        verifyMove(finalPoint);
    }

    @Override
    protected ClientState executeMove(Cell position) {
        this.getPosition().getGameTable().setAthenaMove(position.getHeight().compareTo(getPosition().getHeight()) == 1);
        super.executeMove(position);
        return BUILD;
    }
}
