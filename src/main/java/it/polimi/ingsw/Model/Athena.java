package it.polimi.ingsw.Model;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;

import static it.polimi.ingsw.Client.ClientState.BUILD;
import static it.polimi.ingsw.Client.ClientState.WAIT;

@God(name = "ATHENA")
public class Athena extends Builder {
    Athena(Cell position, Player player) {
        super(position, player);
    }

    private static boolean athenaMove = false;

    @Override
    protected boolean moveHandicap(Cell finalPoint, Cell startingPoint) {
        return (athenaMove && finalPoint.getHeight().compareTo(startingPoint.getHeight()) >= 1);
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
        verifyMove(finalPoint);
    }

    @Override
    protected ClientState executeMove(Cell position) {
        athenaMove = position.getHeight().compareTo(getPosition().getHeight()) >= 1;
        //  this.getPosition().getGameTable().setAthenaMove(position.getHeight().compareTo(getPosition().getHeight()) == 1);
        super.executeMove(position);
        return BUILD;
    }
}
