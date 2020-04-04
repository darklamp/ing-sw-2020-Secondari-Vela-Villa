package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;

public class Athena extends Builder{
    public Athena(Cell position, Player player){
        super(position,player);
    }
    @Override
    public void isValidBuild(Cell cell, BuildingType newheight) throws InvalidBuildException, AtlasException, DemeterException, HephaestusException {
        super.isValidBuild(cell, newheight);
        verifyBuild(cell,newheight);
    }
    @Override
    public void isValidMove(Cell finalPoint) throws MinotaurException, ApolloException, InvalidMoveException, ArtemisException, PrometeusException, PanException {
        super.isValidMove(finalPoint);
        verifyMove(finalPoint);
        if (finalPoint.getHeight().compareTo(getPosition().getHeight()) == 1) GameTable.setAthenaMove(true);
        else GameTable.setAthenaMove(false);
    }
}
