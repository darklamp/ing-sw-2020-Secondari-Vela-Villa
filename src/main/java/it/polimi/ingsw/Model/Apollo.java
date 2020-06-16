package it.polimi.ingsw.Model;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;

import static it.polimi.ingsw.Client.ClientState.BUILD;
import static it.polimi.ingsw.Client.ClientState.WAIT;

@God(name = "APOLLO")
public class Apollo extends Builder{
    Apollo(Cell position, Player player) {
        super(position,player);
    }

    @Override
    protected ClientState isValidBuild(Cell cell, BuildingType newheight) throws InvalidBuildException {
        super.isValidBuild(cell, newheight);
        verifyBuild(cell, newheight);
        return WAIT;
    }

    @Override
    protected void isValidMove(Cell finalPoint) throws InvalidMoveException {
        super.isValidMove(finalPoint);
        if (!(finalPoint.getBuilder() != null && finalPoint.getBuilder().getPlayer() != this.getPlayer()))
            verifyMove(finalPoint);
    }

    @Override
    protected ClientState executeMove(Cell position) {
        if (position.getBuilder() != null) swapPosition(this, position.getBuilder());
        else super.executeMove(position);
        return BUILD;
    }
}
