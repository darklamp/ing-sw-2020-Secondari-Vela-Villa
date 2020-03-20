package it.polimi.ingsw.Model.God;

import it.polimi.ingsw.Model.Builder;
import it.polimi.ingsw.Model.BuildingType;
import it.polimi.ingsw.Model.Cell;
import it.polimi.ingsw.Model.Exceptions.*;

public abstract class GodDecorator implements God {
    private Builder builder;

    public GodDecorator(Builder builder) {
        this.builder = builder;
    }

    public void isValidBuild(BuildingType oldheight, BuildingType newheight) throws BuildingOnDomeException, AtlasException, InvalidBuildException {
        builder.isValidBuild(oldheight, newheight);
    }
    public void isValidMove(Cell finalPoint) throws InvalidMoveException, MoveOnOccupiedCellException {
        builder.isValidMove(finalPoint);
    }
}
