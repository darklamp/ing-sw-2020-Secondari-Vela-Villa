package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;

import javax.swing.*;

public class Prometeus extends Builder {
    public Prometeus(Cell position, Player player) {
        super(position,player);
    }

    @Override
    public void isValidBuild(Cell cell, BuildingType newheight) throws InvalidBuildException, AtlasException, DemeterException, HephaestusException {
        super.isValidBuild(cell, newheight);
        verifyBuild(cell,newheight);
    }

    @Override
    public void isValidMove(Cell finalPoint) throws MinotaurException, ApolloException, InvalidMoveException, ArtemisException, PrometeusException {
        super.isValidMove(finalPoint);
        if (getPosition().getHeight().compareTo(finalPoint.getHeight()) >= 0 && finalPoint.getBuilder()!=null) throw new PrometeusException();
        else verifyMove(finalPoint);
    }

    public void BuildBefore(Cell cell){  //the player can build in a cell near position,the move and the other construction can be done normally(he can't go up)
        if (getPosition().getNear().contains(cell)){
            getPlayer().Build(cell);
        }
    }
}


