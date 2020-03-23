package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;

import java.util.ArrayList;

public class Cell {

    private Builder builder = null;

    private final Pair coordinates;

    private BuildingType height = BuildingType.NONE;

    public Cell(int x, int y) { // constructor for Cell
        this.coordinates = new Pair(x,y);
    }

    /**
     * @return X coordinate of the given cell
     */
    public int getX() {
        return coordinates.getFirst();
    }

    /**
     * @return Y coordinate of the given cell
     */
    public int getY() {
        return coordinates.getSecond();
    }

    /**
     * @return coordinates of the given cell, in form of Pair
     */
    public Pair getPosition() {
        return coordinates;
    }

    /**
     * @return height of the given cell
     */
    public BuildingType getHeight() {
        return height;
    }

    /**
     * @param builder represents the builder which is trying to build on the cell
     * @param height represents the height at which the builder wants to build
     * @throws InvalidBuildException if the cell is occupied by another builder OR if the cell is not adjacent
     * @throws AtlasException Atlas' case, in which the builder is building a dome wherever he wants (conditions are checked)
     */
    public void setHeight(Builder builder, BuildingType height) throws InvalidBuildException, AtlasException {
            try {
                if (this.builder != null) throw new InvalidBuildException(); // there's a builder on the cell, so I can't build on it
                else if (!(builder.getPosition().getNear().contains((this.coordinates)))) throw new InvalidBuildException(); // trying to build on a non-near cell
                builder.isValidBuild(this, height);
                this.height = height; //OK
            } catch (NullPointerException e) {
                e.printStackTrace(); // unhandled error
            }  catch (AtlasException e) { // the player is trying to build dome
                this.height = BuildingType.DOME;
                //TODO verificare correttezza
                // notify caller ?
            }
            catch (HephaestusException e) { //TODO
            }
            catch (DemeterException e){
                //TODO
            }
    }


    /**
     * @return list of Pairs of coordinates (x,y) near the given cell
     * for example, if my cell has coord (0,0), the resulting list contains (1,0), (1,1), (0,1)
     */
    protected ArrayList<Pair> getNear() { // returns cell numbers near given cell
        ArrayList<Pair> out = new ArrayList<>();
        for (int i = coordinates.getFirst() - 1; i <= (coordinates.getFirst() + 1); i++) {
            if (i >= 0 && i < 5) {
                for (int j = coordinates.getSecond() - 1; j <= (coordinates.getSecond() + 1); j++) {
                    if (j >= 0 && j < 5) {
                        if (j != coordinates.getSecond() || i != coordinates.getFirst()) {
                            out.add(new Pair(i,j));
                        }
                    }
                }
            }
        }
        return out;
    }


    public void setBuilder(Builder builder) {
        this.builder = builder;
    }

    /**
     * @param x X coordinate of the cell to be checked
     * @param y Y  ..
     * @return true if the Cell is empty (no builder nor dome), else false
     */
    protected boolean movableCell(int x, int y){
        if(x>4 || x<0 || y>4 || y<0) throw new UnsupportedOperationException();
        else {
            try {
                return GameTable.getCell(x, y).getBuilder() == null && (height != BuildingType.DOME);
            }
            catch (InvalidCoordinateException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    protected Builder getBuilder() {
        return this.builder;
    }

}
