package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;

public class Pan extends Builder{

    public Pan(Cell position, Player player) {
        super(position, player);
    }

    public void isValidBuild(Cell cell, BuildingType newheight) throws InvalidBuildException, AtlasException, DemeterException, HephaestusException {
        super.isValidBuild(cell, newheight);
        verifyBuild(cell,newheight);
    }


    /**
     * @param finalPoint represents the cell to which the builder wants to move
     * @throws MinotaurException n/a
     * @throws ApolloException n/a
     * @throws InvalidMoveException see super
     * @throws PrometeusException n/a
     * @throws ArtemisException n/a
     * @throws PanException when builder goes down by two or more levels --> players wins game
     */
    @Override
    public void isValidMove(Cell finalPoint) throws MinotaurException, ApolloException, InvalidMoveException, PrometeusException, ArtemisException, PanException {
        super.isValidMove(finalPoint);
        verifyMove(finalPoint);
        if (this.getPosition().getHeight().compareTo(finalPoint.getHeight()) >= 2){
            throw new PanException();
        }
    }
}
