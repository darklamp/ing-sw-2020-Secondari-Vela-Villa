package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.BuildingMoreLevelsException;
import it.polimi.ingsw.Model.Exceptions.BuildingOnDomeException;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;

import java.util.ArrayList;

public class Cell {

    private Builder builder = null;

    private final Pair coordinates;

    private BuildingType height = BuildingType.NONE;

    public Cell(int x, int y) { // constructor for Cell
        this.coordinates = new Pair(x,y);
    }

    public int getX() {
        return coordinates.getFirst();
    }

    public int getY() {
        return coordinates.getSecond();
    }

    public Pair getPosition() {
        return coordinates;
    }

    public BuildingType getHeight() {
        return height;
    }

    public void setHeight(BuildingType height) throws InvalidBuildException, BuildingOnDomeException, BuildingMoreLevelsException {
        if (builder != null) throw new InvalidBuildException(); // there's a builder on the cell, so I can't build on it
        else {
            try {
                Builder.isValidBuild(this.height, height);
                this.height = height; //OK
            } catch (NullPointerException e) {
                e.printStackTrace(); // unhandled error
            } catch (BuildingOnDomeException e) { // building on dome isn't possible
                throw new BuildingOnDomeException(); // notify caller
            } catch (BuildingMoreLevelsException e) { // the player is trying to build up more than one level
                throw new BuildingMoreLevelsException(); // notify caller
            } catch (InvalidBuildException e) { // invalid build action from player
                throw new InvalidBuildException(); // notify caller
            }
        }
    }


    public ArrayList<Pair> getNear() { // returns cell numbers near given cell
        ArrayList<Pair> out = new ArrayList<>();
        for (int i = coordinates.getFirst() - 1; i <= (coordinates.getFirst() + 1) ; i++) {
            if (i >= 0 && i <= 5) {
                for (int j = coordinates.getSecond() - 1; j <= (coordinates.getSecond() + 1); j++) {
                    if (j >= 0 && j <= 5) {
                        if (j != coordinates.getSecond() && i != coordinates.getFirst()) {
                            out.add(new Pair(i,j));
                        }
                    }
                }
            }
        }
        return out;
    }

    public Builder getBuilder() {
        return builder;
    }

    public void setBuilder(Builder builder) {
        this.builder = builder;
    }

}
