package it.polimi.ingsw.Model;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;

import static it.polimi.ingsw.Client.ClientState.WAIT;

@God(name = "ATLAS")
public class Atlas extends Builder{
    Atlas(Cell position, Player player){
        super(position,player);
    }

    @Override
    protected ClientState isValidBuild(Cell cell, BuildingType newheight) throws InvalidBuildException {
        super.isValidBuild(cell, newheight);
        if (!(newheight.equals(BuildingType.DOME) && !cell.getHeight().equals(BuildingType.DOME)))
            verifyBuild(cell, newheight);
        return WAIT;
    }

    @Override
    public void isValidMove(Cell finalPoint) throws InvalidMoveException {
        super.isValidMove(finalPoint);
        verifyMove(finalPoint);
    }
}