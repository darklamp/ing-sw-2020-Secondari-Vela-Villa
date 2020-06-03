package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;
import it.polimi.ingsw.Utility.Pair;
import it.polimi.ingsw.View.CellView;

import java.io.Serializable;
import java.util.ArrayList;

import static java.util.stream.Collectors.toCollection;

public class Cell implements Serializable {

    private static final long serialVersionUID = 17756L;

    private Builder builder = null;

    private final Pair coordinates;

    private final GameTable gameTable;

    private BuildingType height = BuildingType.NONE;

    public Cell(int x, int y, GameTable gameTable) { // constructor for Cell
        this.gameTable = gameTable;
        this.coordinates = new Pair(x, y);
    }

    /**
     * Creates a {@link CellView} for the Cell.
     *
     * @param table {@link GameTable} on which the Cell stands.
     * @return {@link CellView} representation of given Cell.
     */
    CellView getModelView(GameTable table) {
        return new CellView(height, builder == null ? -1 : table.getPlayerIndex(builder.getPlayer()), builder != null && builder.isFirst());
    }


    /**
     * Forces height on Cell without any check. Used in tests only, hence tagged as deprecated.
     **/
    @Deprecated
    void mustSetHeight(BuildingType height) {
        this.height = height;
    }

    /**
     * This function is responsible for setting a cell's height, which translates to building on a cell.
     *
     * @param builder represents the builder which is trying to build on the cell
     * @param height  represents the height at which the builder wants to build
     * @throws InvalidBuildException if the cell is occupied by another builder OR if the cell is not adjacent
     * @throws DemeterException      --> {@link Demeter}
     * @throws HephaestusException   --> {@link Hephaestus}
     * @throws PrometheusException   --> {@link Prometheus}
     */
    public void setHeight(Builder builder, BuildingType height) throws InvalidBuildException, DemeterException, PrometheusException, HephaestusException {
        try {

            if (height == null) {
                height = this.height.getNext();
            }

            if (!(builder.getPosition().getNear().contains((this))))
                throw new InvalidBuildException(); // trying to build on a non-near cell

            builder.isValidBuild(this, height);

            this.height = height; //OK

        } catch (AtlasException e) { // the player is trying to build dome
            this.height = BuildingType.DOME;
        } catch (HephaestusException e) {
            this.height = height; //OK
            throw new HephaestusException();
        } catch (PrometheusException e) {
            this.height = height;
            throw new PrometheusException();
        } catch (DemeterException e) {
            this.height = height;
            throw new DemeterException();
        }
    }

    /**
     * Array of nearby cells, saved to save useless computation.
     * By design, it's not hardcoded for every cell, but rather
     * created the first time {@link Cell#getNear()} is called.
     */
    private ArrayList<Cell> nearbyCells = null;

    /**
     * @return list of cells which are near the given cell.
     * @example if my cell has coord (0,0), the resulting list contains (1,0), (1,1), (0,1)
     */
    protected ArrayList<Cell> getNear() {
        if (nearbyCells == null)
            nearbyCells = gameTable.toArrayList().stream().filter(cell -> cell.isNear(this)).collect(toCollection(ArrayList::new));
        return nearbyCells;
    }

    /**
     * This simple function checks whether the  " other "  Cell is near to this.
     * It is a helper function to getNear().
     *
     * @param other {@link Cell} to be checked for nearness
     * @return true if near, false otherwise
     */
    private boolean isNear(Cell other){
        if (this.getRow() == other.getRow() - 1 || this.getRow() == other.getRow() + 1) {
            return this.getColumn() - other.getColumn() <= 1 && this.getColumn() - other.getColumn() >= -1;
        } else if (this.getColumn() == other.getColumn() - 1 || this.getColumn() == other.getColumn() + 1) {
            return this.getRow() - other.getRow() <= 1 && this.getRow() - other.getRow() >= -1;
        }
        return false;
    }

    /**
     * Checks whether the given cell is not already occupied
     * @param x X coordinate of the cell to be checked
     * @param y Y coordinate of the cell to be checked
     * @return true if the Cell has no builder nor dome on it, else false
     * @throws InvalidCoordinateException if the given coordinates are invalid
     */
    boolean movableCell(int x, int y) throws InvalidCoordinateException{
        return gameTable.getCell(x,y).getBuilder() == null && (gameTable.getCell(x,y).getHeight() != BuildingType.DOME);
    }


    /** simple getters / setters **/


    public void setBuilder(Builder builder) {
        this.builder = builder;
    }

    public Builder getBuilder() {
        return this.builder;
    }

    public int getRow() {
        return coordinates.getFirst();
    }

    public int getColumn() {
        return coordinates.getSecond();
    }

    public Pair getPosition() {
        return coordinates;
    }

    public BuildingType getHeight() {
        return height;
    }

    GameTable getGameTable() {
        return gameTable;
    }

}
