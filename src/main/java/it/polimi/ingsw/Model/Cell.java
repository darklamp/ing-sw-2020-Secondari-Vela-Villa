package it.polimi.ingsw.Model;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;
import it.polimi.ingsw.Model.Exceptions.NoMoreMovesException;
import it.polimi.ingsw.Utility.Pair;
import it.polimi.ingsw.View.CellView;

import java.io.Serializable;
import java.util.ArrayList;

import static it.polimi.ingsw.Client.ClientState.BUILD;
import static it.polimi.ingsw.Client.ClientState.WAIT;
import static java.util.stream.Collectors.toCollection;

/**
 * Represents a cell in a {@link GameTable} object
 */
public class Cell implements Serializable {

    private static final long serialVersionUID = 17756L;

    private Builder builder = null;

    private final Pair coordinates;

    private final GameTable gameTable;

    private BuildingType height = BuildingType.NONE;

    /**
     * @param x         x coord
     * @param y         y coord
     * @param gameTable table on which the cell is
     */
    protected Cell(int x, int y, GameTable gameTable) {
        this.gameTable = gameTable;
        this.coordinates = new Pair(x, y);
    }

    /**
     * Creates a {@link CellView} for the Cell.
     *
     * @return {@link CellView} representation of given Cell.
     */
    CellView getModelView() {
        return new CellView(height, builder == null ? -1 : gameTable.getPlayerIndex(builder.getPlayer()), builder != null && builder.isFirst());
    }


    /**
     * This function is responsible for setting a cell's height, which translates to building on a cell.
     *
     * @param builder represents the builder which is trying to build on the cell
     * @param height  represents the height at which the builder wants to build
     * @throws InvalidBuildException if the cell is occupied by another builder OR if the cell is not adjacent
     * @throws NoMoreMovesException  if the build goes through and the next player has no more moves available
     */
    public void setHeight(Builder builder, BuildingType height) throws InvalidBuildException, NoMoreMovesException {
        ClientState nextState;
        News news = new News();
        try {
            gameTable.isLegalState(BUILD, builder.getPlayer());
        } catch (Exception e) {
            news.setRecipients(builder.getPlayer());
            getGameTable().setNews(news, "BUILDKO");
            throw new InvalidBuildException();
        }
        boolean flag = false;
        try {
            if (height == null) {
                height = this.height.getNext();
            }
            nextState = builder.isValidBuild(this, height);
        } catch (InvalidBuildException e) {
            news.setRecipients(builder.getPlayer());
            getGameTable().setNews(news, "BUILDKO");
            throw e;
        }
        if (nextState != WAIT) flag = true;
        this.height = height;
        gameTable.getCurrentPlayer().setState(nextState);
        gameTable.getCurrentPlayer().setFirstTime(false);
        gameTable.setCurrentBuilder(builder);
        if (!flag) gameTable.nextTurn();
        else {
            gameTable.checkConditions();
            getGameTable().setNews(news, "BUILDOK");
        }
    }

    /**
     * Array of nearby cells, saved to save useless computation.
     * By design, it's not hardcoded for every cell, but rather
     * created the first time {@link Cell#getNear()} is called.
     */
    private ArrayList<Cell> nearbyCells = null;

    /**
     * Finds cells near the given one.
     *
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
     *
     * @param x X coordinate of the cell to be checked
     * @param y Y coordinate of the cell to be checked
     * @return true if the Cell has no builder nor dome on it, else false
     * @throws InvalidCoordinateException if the given coordinates are invalid
     */
    boolean movableCell(int x, int y) throws InvalidCoordinateException {
        return gameTable.getCell(x, y).getBuilder() == null && (gameTable.getCell(x, y).getHeight() != BuildingType.DOME);
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
