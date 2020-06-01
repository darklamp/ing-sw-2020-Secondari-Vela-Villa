package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;

public class Athena extends Builder {
    Athena(Cell position, Player player) {
        super(position, player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void isValidBuild(Cell cell, BuildingType newheight) throws InvalidBuildException, AtlasException, DemeterException, HephaestusException, PrometheusException {
        super.isValidBuild(cell, newheight);
        verifyBuild(cell, newheight);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void isValidMove(Cell finalPoint) throws InvalidMoveException, PanException, ApolloException, ArtemisException, MinotaurException {
        super.isValidMove(finalPoint);
        verifyMove(finalPoint);
        this.getPosition().getGameTable().setAthenaMove(finalPoint.getHeight().compareTo(getPosition().getHeight()) == 1);
    }
}
