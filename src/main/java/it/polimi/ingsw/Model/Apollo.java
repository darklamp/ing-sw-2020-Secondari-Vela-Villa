package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;

public class Apollo extends Builder{
    Apollo(Cell position, Player player) {
        super(position,player);
    }


    /**
     * combines pre + postconditions
     */
    @Override
    protected void isValidBuild(Cell cell, BuildingType newheight) throws InvalidBuildException, AtlasException, DemeterException, HephaestusException, PrometheusException {
        super.isValidBuild(cell, newheight);
        verifyBuild(cell, newheight);
    }

    /**
     * @param finalPoint indicates the point to which the builder is trying to move
     * @throws InvalidMoveException can be thrown by the super() method; see Builder.isValidMove()
     * @throws ApolloException gets thrown if the builder tries to move to a cell occupied by an enemy builder
     */
    @Override
    void isValidMove(Cell finalPoint) throws InvalidMoveException, ApolloException, MinotaurException, PrometheusException, ArtemisException, PanException {
            super.isValidMove(finalPoint);
            if (finalPoint.getBuilder() != null && finalPoint.getBuilder().getPlayer() != this.getPlayer()) throw new ApolloException();
            else verifyMove(finalPoint);
    }
}
