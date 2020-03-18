package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.BuildingMoreLevelsException;
import it.polimi.ingsw.Model.Exceptions.BuildingOnDomeException;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;

public class Builder /*implements God*/ {

    private Cell position;

    public Builder(Cell position) { // constructor for Builder with Cell parameter
        if(position == null) throw new NullPointerException();
        else this.position = position;
    }

    public Cell getPosition() {
        return position;
    }

    public void setPosition(Cell position) {
        try {
            isValidMove(position); // check validity of move
            this.position = position;
        } catch (NullPointerException e) {
            e.printStackTrace(); // unhandled error
        } catch (InvalidMoveException e) {
            //TODO
        }
    }


    protected static void isValidBuild(BuildingType oldheight, BuildingType newheight) throws BuildingOnDomeException,BuildingMoreLevelsException,InvalidBuildException {

        if (oldheight.equals(BuildingType.DOME)) throw new BuildingOnDomeException(); // verify that there is no dome on the cell
        else if (newheight.compareTo(oldheight) <= 0)  throw new InvalidBuildException(); // verify that I'm not building on the same level as already present or on a lower level
        else if (oldheight.equals(BuildingType.TOP) && !(newheight.equals(BuildingType.DOME))) throw new InvalidBuildException(); // if a top level is present, only a dome can be placed
        else if (newheight.compareTo(oldheight) >= 2) throw new BuildingMoreLevelsException(); // building more than one level; might be feasible, see Atlas' power

    }

    private void isValidMove(Cell finalPoint) throws InvalidMoveException {

        if(finalPoint == null || finalPoint.getX() < 0 || finalPoint.getX() > 5 || finalPoint.getY() < 0 || finalPoint.getY() > 5) throw new InvalidMoveException(); //out of bounds
        else if (finalPoint.getBuilder() != null || finalPoint.getHeight() == BuildingType.DOME) throw new InvalidMoveException(); // moving on occupied cell or on dome
        else if (finalPoint.getHeight().compareTo(position.getHeight()) >= 2) throw new InvalidMoveException(); // check if I'm moving up more than one level
        else if (!(position.getNear().contains(finalPoint.getPosition()))) throw new InvalidMoveException(); // check that the cell I'm moving to is adjacent

    }

}
