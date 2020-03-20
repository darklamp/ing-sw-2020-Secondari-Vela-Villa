package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.AtlasException;
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

    /**
     * @param builder represents the builder which is trying to build on the cell
     * @param height represents the height at which the builder wants to build
     * @throws InvalidBuildException if the cell is occupied by another builder OR if the cell is not adjacent
     * @throws BuildingOnDomeException if I'm trying to build on a dome; TODO: consider deleting if useless
     * @throws AtlasException Atlas' case, in which the builder is building a dome wherever he wants (conditions are checked)
     */
    public void setHeight(Builder builder, BuildingType height) throws InvalidBuildException, BuildingOnDomeException, AtlasException {
            try {
                if (this.builder != null) throw new InvalidBuildException(); // there's a builder on the cell, so I can't build on it
                else if (!(builder.getPosition().getNear().contains((this)))) throw new InvalidBuildException(); // trying to build on a non-near cell
                builder.isValidBuild(this.height, height);
                this.height = height; //OK
            } catch (NullPointerException e) {
                e.printStackTrace(); // unhandled error
            } catch (BuildingOnDomeException e) { // building on dome isn't possible
                throw new BuildingOnDomeException(); // notify caller
            } catch (AtlasException e) { // the player is trying to build up more than one level
                throw new AtlasException(); // notify caller
            } catch (InvalidBuildException e) { // invalid build action from player
                throw new InvalidBuildException(); // notify caller
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

    public boolean emptyCell(int x, int y){
        if(x>4 || x<0 || y>4 || y<0) throw new UnsupportedOperationException();
        else {
            return getBuilder(x, y) == null;
        }
    }

    private Builder getBuilder(int x, int y) {
        return this.builder;
    }

}
