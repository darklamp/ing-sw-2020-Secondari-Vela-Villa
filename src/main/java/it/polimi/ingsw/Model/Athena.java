package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;

public class Athena extends Builder{
    Athena(Cell position, Player player){
        super(position,player);
    }
    @Override
    public void isValidBuild(Cell cell, BuildingType newheight) throws InvalidBuildException, AtlasException, DemeterException, HephaestusException, PrometheusException {
        super.isValidBuild(cell, newheight);
        verifyBuild(cell,newheight);
    }

    /**
     * If Athena goes up a level, this method sets {@link GameTable# athenaMove} to true
     * if she doesn't, it gets set back to false
     *
     * @param finalPoint represents the cell to which the builder wants to move
     * @throws InvalidMoveException see super
     */
    @Override
    void isValidMove(Cell finalPoint) throws InvalidMoveException, PanException, ApolloException, ArtemisException, MinotaurException {
        super.isValidMove(finalPoint);
        verifyMove(finalPoint);
        GameTable.setAthenaMove(finalPoint.getHeight().compareTo(getPosition().getHeight()) == 1);
    }
}
