package it.polimi.ingsw.Model;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;

import static it.polimi.ingsw.Client.ClientState.MOVE;
import static it.polimi.ingsw.Client.ClientState.WAIT;

public class Prometheus extends Builder {
    Prometheus(Cell position, Player player) {
        super(position, player);
    }

    @Override
    public ClientState isValidBuild(Cell cell, BuildingType newheight) throws InvalidBuildException {
        super.isValidBuild(cell, newheight);
        verifyBuild(cell, newheight);
        return this.getPlayer().getState() == ClientState.MOVEORBUILD ? MOVE : WAIT;
    }

    @Override
    void isValidMove(Cell finalPoint) throws InvalidMoveException {
        super.isValidMove(finalPoint);
        verifyMove(finalPoint);
        if (this.getPlayer().getState() == MOVE) {
            if (getPosition().getHeight().compareTo(finalPoint.getHeight()) < 0) throw new InvalidMoveException();
        }
    }

    @Override
    public ClientState getFirstState(){
        return ClientState.MOVEORBUILD;
    }

}


