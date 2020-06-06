package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;

public class Apollo extends Builder{
    Apollo(Cell position, Player player) {
        super(position,player);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void isValidBuild(Cell cell, BuildingType newheight) throws InvalidBuildException, AtlasException, DemeterException, HephaestusException, PrometheusException {
        super.isValidBuild(cell, newheight);
        verifyBuild(cell, newheight);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void isValidMove(Cell finalPoint) throws InvalidMoveException, ApolloException, MinotaurException, ArtemisException, PanException {
        super.isValidMove(finalPoint);
        if (finalPoint.getBuilder() != null && finalPoint.getBuilder().getPlayer() != this.getPlayer())
            throw new ApolloException();
        else verifyMove(finalPoint);
    }
}
