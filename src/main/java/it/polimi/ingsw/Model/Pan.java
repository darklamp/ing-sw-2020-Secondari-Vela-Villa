package it.polimi.ingsw.Model;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;

import static it.polimi.ingsw.Client.ClientState.*;

public class Pan extends Builder{

    Pan(Cell position, Player player) {
        super(position, player);
    }

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
        ClientState out = this.getPosition().getHeight().compareTo(position.getHeight()) >= 2 ? WIN : BUILD;
        if (out == WIN) return WIN;
        else {
            super.executeMove(position);
            return BUILD;
        }
    }
}
