package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Builder;
import it.polimi.ingsw.Model.BuildingType;
import it.polimi.ingsw.Model.Cell;
import it.polimi.ingsw.Model.Exceptions.*;

public class NoGod extends Builder {
    public NoGod(Cell position) {
        super(position);
    }
  public void isValidBuild(BuildingType oldheight, BuildingType newheight) throws AtlasException, InvalidBuildException {
      if (newheight.compareTo(oldheight) >= 2) throw new InvalidBuildException();
      else if (newheight.compareTo(oldheight) <= 0)  throw new InvalidBuildException();
      else super.isValidBuild(oldheight,newheight);
  }
    public void isValidMove(Cell finalPoint) throws InvalidMoveException, ApolloException, MinotaurException {
      if (finalPoint.getBuilder() != null) throw new InvalidMoveException();
      else super.isValidMove(finalPoint);
    }
}
