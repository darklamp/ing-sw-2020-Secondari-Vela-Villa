package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;

public class Athena extends Builder{
    Athena(Cell position, Player player){
        super(position,player);
    }
    @Override
    public void isValidBuild(Cell cell, BuildingType newheight) throws InvalidBuildException, AtlasException, DemeterException, HephaestusException {
        super.isValidBuild(cell, newheight);
        verifyBuild(cell,newheight);
    }

    /**
     * If Athena goes up a level, this method sets {@link GameTable#athenaMove} to true
     * if she doesn't, it gets set back to false
     * @param finalPoint represents the cell to which the builder wants to move
     * @throws InvalidMoveException see super
     */
    @Override
    void isValidMove(Cell finalPoint) throws InvalidMoveException {

        if(finalPoint == null || finalPoint.getX() < 0 || finalPoint.getX() > 4 || finalPoint.getY() < 0 || finalPoint.getY() > 4) throw new InvalidMoveException(); //out of bounds
        else if (finalPoint.getHeight() == BuildingType.DOME) throw new InvalidMoveException(); // moving on dome
        else if (finalPoint.getHeight().compareTo(this.getPosition().getHeight()) >= 2) throw new InvalidMoveException(); // check if I'm moving up more than one level
        else if (!(this.getPosition().getNear().contains(finalPoint))) throw new InvalidMoveException(); // check that the cell I'm moving to is adjacent

        verifyMove(finalPoint);
        if (finalPoint.getHeight().compareTo(getPosition().getHeight()) == 1) GameTable.setAthenaMove(true);
        else GameTable.setAthenaMove(false);
    }
}
