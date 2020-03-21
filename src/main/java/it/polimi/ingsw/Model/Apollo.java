package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Builder;
import it.polimi.ingsw.Model.BuildingType;
import it.polimi.ingsw.Model.Cell;
import it.polimi.ingsw.Model.Exceptions.*;
import it.polimi.ingsw.Model.Player;

public class Apollo extends Builder{
    public Apollo(Cell position, Player player) {
        super(position,player);
    }


    @Override
    public void isValidBuild(BuildingType oldheight, BuildingType newheight) throws InvalidBuildException, AtlasException {
        super.isValidBuild(oldheight, newheight);
        verifyBuild(oldheight, newheight);
    }

    /**
     * @param finalPoint indicates the point to which the builder is trying to move
     * @throws InvalidMoveException can be thrown by the super() method; see Builder.isValidMove()
     * @throws ApolloException gets thrown if the builder tries to move to a cell occupied by an enemy builder
     */
    @Override
    public void isValidMove(Cell finalPoint) throws InvalidMoveException, ApolloException, MinotaurException {
            super.isValidMove(finalPoint);
            if (finalPoint.getBuilder() != null && finalPoint.getBuilder().getPlayer() != this.getPlayer()) throw new ApolloException();
            else verifyMove(finalPoint);
    }
}
