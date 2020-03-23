package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;

public class Atlas extends Builder{
    public Atlas(Cell position, Player player){
        super(position,player);
    }

    /**
     * @param cell see super
     * @param newheight see super
     * @throws AtlasException when I'm doing something only Atlas can do, aka building a dome on any level
     * @throws InvalidBuildException see super
     */
    @Override
    public void isValidBuild(Cell cell, BuildingType newheight) throws AtlasException, InvalidBuildException, DemeterException, HephaestusException {
        super.isValidBuild(cell,newheight);
        if (newheight.equals(BuildingType.DOME) && !cell.getHeight().equals(BuildingType.DOME)) throw new AtlasException();
        else verifyBuild(cell,newheight);
    }

    @Override
    public void isValidMove(Cell finalPoint) throws InvalidMoveException, ApolloException, MinotaurException, PrometeusException, ArtemisException {
        super.isValidMove(finalPoint);
        verifyMove(finalPoint);
    }
}