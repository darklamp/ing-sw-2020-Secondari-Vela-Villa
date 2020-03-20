package it.polimi.ingsw.Model.God;

import it.polimi.ingsw.Model.Builder;
import it.polimi.ingsw.Model.BuildingType;
import it.polimi.ingsw.Model.Cell;
import it.polimi.ingsw.Model.Exceptions.*;

public class Atlas extends GodDecorator{
    public Atlas(Builder builder){
        super(builder);
    }
    public void isValidBuild(BuildingType oldheight, BuildingType newheight) throws BuildingOnDomeException, BuildingMoreLevelsException, InvalidBuildException {
        if (newheight.equals(BuildingType.DOME) && !oldheight.equals(BuildingType.DOME)) throw new BuildingMoreLevelsException();
        else super.isValidBuild(oldheight,newheight);
    }
    public void isValidMove(Cell finalPoint) throws InvalidMoveException, MoveOnOccupiedCellException {
        if (finalPoint.getBuilder() != null) throw new InvalidMoveException();
        else super.isValidMove(finalPoint);
    }
}
