package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;

public class Pan extends Builder{

    Pan(Cell position, Player player) {
        super(position, player);
    }

    /**
     * {@inheritDoc}
     */
    public void isValidBuild(Cell cell, BuildingType newheight) throws InvalidBuildException, AtlasException, DemeterException, HephaestusException, PrometheusException {
        super.isValidBuild(cell, newheight);
        verifyBuild(cell,newheight);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    void isValidMove(Cell finalPoint) throws MinotaurException, ApolloException, InvalidMoveException, ArtemisException, PanException {
        super.isValidMove(finalPoint);
        verifyMove(finalPoint);
        if (this.getPosition().getHeight().compareTo(finalPoint.getHeight()) >= 2) {
            throw new PanException();
        }
    }
}
