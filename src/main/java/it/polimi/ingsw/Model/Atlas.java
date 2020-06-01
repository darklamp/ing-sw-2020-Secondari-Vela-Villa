package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;

public class Atlas extends Builder{
    Atlas(Cell position, Player player){
        super(position,player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void isValidBuild(Cell cell, BuildingType newheight) throws AtlasException, InvalidBuildException, DemeterException, HephaestusException, PrometheusException {
        super.isValidBuild(cell, newheight);
        if (newheight.equals(BuildingType.DOME) && !cell.getHeight().equals(BuildingType.DOME))
            throw new AtlasException();
        else verifyBuild(cell, newheight);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void isValidMove(Cell finalPoint) throws InvalidMoveException, ApolloException, MinotaurException, ArtemisException, PanException {
        super.isValidMove(finalPoint);
        verifyMove(finalPoint);
    }
}