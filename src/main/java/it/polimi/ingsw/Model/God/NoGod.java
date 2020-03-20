package it.polimi.ingsw.Model.God;

import it.polimi.ingsw.Model.Builder;
import it.polimi.ingsw.Model.BuildingType;
import it.polimi.ingsw.Model.Cell;
import it.polimi.ingsw.Model.Exceptions.*;

public class NoGod extends GodDecorator{
    public NoGod(Builder builder) {
        super(builder);
    }
  public void isValidBuild(BuildingType oldheight, BuildingType newheight) throws BuildingOnDomeException, BuildingMoreLevelsException, InvalidBuildException {
      if (newheight.compareTo(oldheight) >= 2) throw new InvalidBuildException();
      else if (newheight.compareTo(oldheight) <= 0)  throw new InvalidBuildException();
      else super.isValidBuild(oldheight,newheight);
  }
    public void isValidMove(Cell finalPoint) throws InvalidMoveException, MoveOnOccupiedCellException {
      if (finalPoint.getBuilder() != null) throw new InvalidMoveException();
      else super.isValidMove(finalPoint);
    }
}
