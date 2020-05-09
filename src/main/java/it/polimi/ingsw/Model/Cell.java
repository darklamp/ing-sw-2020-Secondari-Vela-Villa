package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;
import it.polimi.ingsw.View.CellView;

import java.io.Serializable;
import java.util.ArrayList;

import static java.util.stream.Collectors.toCollection;

public class Cell implements Serializable {

    private Builder builder = null;

    private final Pair coordinates;

    private final GameTable gameTable;

    private BuildingType height = BuildingType.NONE;

    public Cell(int x, int y, GameTable gameTable) { // constructor for Cell
        this.gameTable = gameTable;
        this.coordinates = new Pair(x,y);
    }

    CellView getModelView(GameTable table){
        return new CellView(height,builder == null ? -1 : table.getPlayerIndex(builder.getPlayer()), builder != null && builder.isFirst());
    }


    // DEBUG function TODO remove in deploy
    void mustSetHeight(BuildingType height) {
        this.height = height; //mette per forza quella costruzione
    }
    /**
     * This function is responsible for setting a cell's height, which translates to building on a cell.
     * @param builder represents the builder which is trying to build on the cell
     * @param height represents the height at which the builder wants to build
     * @throws InvalidBuildException if the cell is occupied by another builder OR if the cell is not adjacent
     * @throws DemeterException --> {@link Demeter}
     */
    public void setHeight(Builder builder, BuildingType height) throws InvalidBuildException, DemeterException, PrometheusException, HephaestusException {
            try {

                if (height == null){
                    height = this.height.getNext();
                }

                if (this.builder != null) throw new InvalidBuildException(); // there's a builder on the cell, so I can't build on it

                else if (!(builder.getPosition().getNear().contains((this)))) throw new InvalidBuildException(); // trying to build on a non-near cell

                builder.isValidBuild(this, height);

                this.height = height; //OK

            } catch (NullPointerException e) {
                e.printStackTrace(); // unhandled error
            }  catch (AtlasException e) { // the player is trying to build dome
                this.height = BuildingType.DOME;
            }
            catch (HephaestusException e) { //TODO notify controller
                this.height = height; //OK
                throw new HephaestusException();
            }
            catch (PrometheusException e){
                this.height = height;
                throw new PrometheusException();
            }
            catch (DemeterException e){
                this.height = height;
                throw new DemeterException();
            }
            catch (InvalidBuildException e){
                throw new InvalidBuildException(); //this is here just for tests TODO remove
            }
    }

    /**
     * @return list of cells which are near the given cell
     * @example if my cell has coord (0,0), the resulting list contains (1,0), (1,1), (0,1)
     */
    protected ArrayList<Cell> getNear() { // returns cell numbers near given cell
        return gameTable.toArrayList().stream().filter(cell -> cell.isNear(this)).collect(toCollection(ArrayList::new));
    }

    /**
     * This simple function checks whether the  " other "  Cell is near to this.
     * It is a helper function to getNear().
     * @return true if near, false otherwise
     */
    private boolean isNear(Cell other){
        if (this.getRow() == other.getRow() - 1 || this.getRow() == other.getRow() + 1){
            return this.getColumn() - other.getColumn() <= 1 && this.getColumn() - other.getColumn() >= -1;
        }
        else if (this.getColumn() == other.getColumn() -1 || this.getColumn() == other.getColumn() + 1){
            return this.getRow() - other.getRow() <= 1 && this.getRow() - other.getRow() >= -1;
        }
        return false;
    }

    /**
     * @param x X coordinate of the cell to be checked
     * @param y Y  ..
     * @return true if the Cell has no builder nor dome on it, else false
     */
    boolean movableCell(int x, int y) throws InvalidCoordinateException{
        return gameTable.getCell(x,y).getBuilder() == null && (gameTable.getCell(x,y).getHeight() != BuildingType.DOME);
    }



    /** getter / setter **/


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

}
