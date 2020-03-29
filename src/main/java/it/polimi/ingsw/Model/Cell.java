package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;

import java.util.ArrayList;

import static java.util.stream.Collectors.toCollection;

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
     */
    public void setHeight(Builder builder, BuildingType height) throws InvalidBuildException, DemeterException {
            try {
                if (this.builder != null) throw new InvalidBuildException(); // there's a builder on the cell, so I can't build on it
                else if (!(builder.getPosition().getNear().contains((this)))) throw new InvalidBuildException(); // trying to build on a non-near cell
                builder.isValidBuild(this, height);
                this.height = height; //OK
            } catch (NullPointerException e) {
                e.printStackTrace(); // unhandled error
            }  catch (AtlasException e) { // the player is trying to build dome
                this.height = BuildingType.DOME;
                //TODO verificare correttezza
                // notify caller ?
            }
            catch (HephaestusException e) { //TODO notify controller
                this.height = height; //OK
            }
            catch (DemeterException e){
                //TODO: notify controller?
                // TEMP per test
                throw new DemeterException();
                // TEMP
            }
    }


    /**
     * @return list of Pairs of coordinates (x,y) near the given cell
     * for example, if my cell has coord (0,0), the resulting list contains (1,0), (1,1), (0,1)
     */
    protected ArrayList<Cell> getNear() { // returns cell numbers near given cell
        return GameTable.toArrayList().stream().filter(Pair -> Pair.isNear(this)).collect(toCollection(ArrayList::new));
    }

    private boolean isNear(Cell other){
        if (this.getX() == other.getX() - 1 || this.getX() == other.getX() + 1){
            return this.getY() - other.getY() <= 1 && this.getY() - other.getY() >= -1;
        }
        else if (this.getY() == other.getY() -1 || this.getY() == other.getY() + 1){
            return this.getX() - other.getX() <= 1 && this.getX() - other.getX() >= -1;
        }
        return false;
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
