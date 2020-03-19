package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;

public interface God {
    void isValidBuild(BuildingType oldheight, BuildingType newheight) throws BuildingOnDomeException, BuildingMoreLevelsException, InvalidBuildException;
    void isValidMove(Cell finalPoint) throws InvalidMoveException, MoveOnOccupiedCellException;
}
