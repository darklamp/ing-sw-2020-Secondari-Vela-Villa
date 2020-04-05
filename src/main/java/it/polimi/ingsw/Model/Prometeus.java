package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;

public class Prometeus extends Builder {
    Prometeus(Cell position, Player player) {
        super(position,player);
    }

    @Override
    public void isValidBuild(Cell cell, BuildingType newheight) throws InvalidBuildException, AtlasException, DemeterException, HephaestusException {
        super.isValidBuild(cell, newheight);
        verifyBuild(cell,newheight);
    }

    @Override
    void isValidMove(Cell finalPoint) throws MinotaurException, ApolloException, InvalidMoveException, ArtemisException, PrometeusException, PanException {
        super.isValidMove(finalPoint);
        verifyMove(finalPoint);
        if (getPosition().getHeight().compareTo(finalPoint.getHeight()) < 0) throw new PrometeusException();
    }

    /*public void BuildBefore(Cell cell){  //the player can build in a cell near position,the move and the other construction can be done normally(he can't go up)
        if (getPosition().getNear().contains(cell)){
            getPlayer().Build(cell);
        }
    }*/
    @Override
    void resetState(){
        this.getPlayer().setState(TurnState.MOVEORBUILD);
    }
}


