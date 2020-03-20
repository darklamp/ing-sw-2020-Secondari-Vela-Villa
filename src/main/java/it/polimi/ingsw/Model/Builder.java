package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;
import it.polimi.ingsw.Model.God.God;

public class Builder implements God {

    private Cell position;

    public Builder(Cell position) { // constructor for Builder with Cell parameter
        if(position == null) throw new NullPointerException();
        else this.position = position;
    }

    public Cell getPosition() {
        return position;
    }

    public void setPosition(Cell position) throws InvalidMoveException {
        try {
            isValidMove(position); // check validity of move
            this.position = position; // sets position if no exceptions are thrown
        } catch (NullPointerException e) {
            e.printStackTrace(); // unhandled error
        } catch (InvalidMoveException e) { // cannot move here
            throw new InvalidMoveException(); // notify caller
        }
        catch (ApolloException e) {
            //TODO
        }
        catch (MinotaurException e) {

        }
    }

    // questa implementazione è la base; su questa poi si vanno a specificare più restrizioni

    public void isValidBuild(BuildingType oldheight, BuildingType newheight) throws BuildingOnDomeException, AtlasException,InvalidBuildException {

        if (oldheight.equals(BuildingType.DOME)) throw new BuildingOnDomeException(); // verify that there is no dome on the cell
        //else if (newheight.compareTo(oldheight) <= 0)  throw new InvalidBuildException(); // verify that I'm not building on the same level as already present or on a lower level ; ATLAS can do this ( with domes only )
        else if (oldheight.equals(BuildingType.TOP) && !(newheight.equals(BuildingType.DOME))) throw new InvalidBuildException(); // if a top level is present, only a dome can be placed

        // else if (newheight.compareTo(oldheight) >= 2) throw new AtlasException(); // building more than one level up; might be feasible, see Atlas' power

    }
//TODO: fare bene

    // questa implementazione è la base; su questa poi si vanno a specificare più restrizioni
    public void isValidMove(Cell finalPoint) throws InvalidMoveException, ApolloException, MinotaurException {

        if(finalPoint == null || finalPoint.getX() < 0 || finalPoint.getX() > 4 || finalPoint.getY() < 0 || finalPoint.getY() > 4) throw new InvalidMoveException(); //out of bounds
        else if (finalPoint.getHeight() == BuildingType.DOME) throw new InvalidMoveException(); // moving on dome
        else if (finalPoint.getHeight().compareTo(position.getHeight()) >= 2) throw new InvalidMoveException(); // check if I'm moving up more than one level
        else if (!(position.getNear().contains(finalPoint.getPosition()))) throw new InvalidMoveException(); // check that the cell I'm moving to is adjacent
     //   else if (finalPoint.getBuilder() != null) throw new ApolloException(); // moving on occupied cell can be done with apollo
    }

    /**
     * @param position specifies position where to force-move the builder
     */
    private void forceMove(Cell position){
        this.position = position;
    }

    protected void swapPosition(Builder firstBuilder, Builder otherBuilder) {
        Cell temp = firstBuilder.getPosition();
        firstBuilder.forceMove(otherBuilder.getPosition());
        otherBuilder.forceMove(temp);
    }

}
