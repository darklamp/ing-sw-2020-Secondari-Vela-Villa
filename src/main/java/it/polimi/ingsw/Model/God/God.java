package it.polimi.ingsw.Model.God;

import it.polimi.ingsw.Model.BuildingType;
import it.polimi.ingsw.Model.Cell;
import it.polimi.ingsw.Model.Exceptions.*;

public interface God {
    void isValidBuild(BuildingType oldheight, BuildingType newheight) throws BuildingOnDomeException, BuildingMoreLevelsException, InvalidBuildException;
    void isValidMove(Cell finalPoint) throws InvalidMoveException, MoveOnOccupiedCellException;
}
