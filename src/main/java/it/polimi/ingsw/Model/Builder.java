package it.polimi.ingsw.Model;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.Exceptions.*;

import java.io.Serializable;

import static it.polimi.ingsw.Client.ClientState.WAIT;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public abstract class Builder implements Serializable {

    private static final long serialVersionUID = 17756L;

    private Cell position;

    private final Player player;

    private boolean first = false;

    public Builder(Cell position, Player player) { // constructor for Builder with Cell parameter
        if (player == null || position == null) throw new NullPointerException();
        else {
            this.player = player;
            this.position = position;
            this.first = player.hasNoBuilder();
            position.setBuilder(this);
        }
    }

    public boolean isFirst(){
        return first;
    }

    /**
     * @return position of given builder, in form of cell
     */
    public Cell getPosition() {
        return position;
    }

    /**
     * Sets position of builder to a given cell.
     *
     * @param position position to move the builder to
     * @throws InvalidMoveException if the move isn't allowed
     * @throws WinnerException      if the move leads to a win
     * @throws ArtemisException     {@link Artemis}
     * @throws PanException         {@link Pan}
     * @throws MinotaurException    {@link Minotaur}
     */
    public void setPosition(Cell position) throws InvalidMoveException, ArtemisException, PanException, WinnerException, MinotaurException {
        try {
            isValidMove(position); // check validity of move
            this.position.setBuilder(null);
            this.position = position; // sets position if no exceptions are thrown
            position.setBuilder(this);
            if (isWinner()) throw new WinnerException(this.player);
        } catch (ApolloException e) { // swap position cause it's Apollo
            swapPosition(this, position.getBuilder());
            if (isWinner()) throw new WinnerException(this.player);
        } catch (ArtemisException e) {
            this.position.setBuilder(null);
            this.position = position; // sets position if no exceptions are thrown
            position.setBuilder(this);
            if (isWinner()) throw new WinnerException(this.player);
            throw new ArtemisException();
        }
    }

    /**
     * Very simply checks whether, after the last valid move, the builder moved to a TOP level.
     *
     * @return true if the client moved to a top level (hence it's the winner)
     */
    public boolean isWinner() {
        return this.position.getHeight() == BuildingType.TOP;
    }

    /**
     * @param cell      represents the cell on which the builder wants to build
     * @param newheight represents "new height", meaning the height which the builder wants to build on
     * @throws InvalidBuildException when the build is illegal
     * @throws AtlasException        --> {@link AtlasException}
     * @throws DemeterException      --> {@link DemeterException}
     * @throws HephaestusException   --> {@link HephaestusException}
     * @throws PrometheusException   --> {@link PrometheusException}
     */
    protected void isValidBuild(Cell cell, BuildingType newheight) throws InvalidBuildException, AtlasException, HephaestusException, DemeterException, PrometheusException {

        if (cell.getHeight().equals(BuildingType.DOME) || cell.getBuilder() != null)
            throw new InvalidBuildException(); // verify that there is no dome on the cell
        else if (cell.getHeight().equals(BuildingType.TOP) && !(newheight.equals(BuildingType.DOME)))
            throw new InvalidBuildException(); // if a top level is present, only a dome can be placed

    }

    /**
     * This method checks postconditions for building
     *
     * @param cell      represents the cell on which the builder wants to build
     * @param newheight represents "new height", meaning the height which the builder wants to build on
     * @throws InvalidBuildException when the build is illegal
     */
    protected void verifyBuild(Cell cell, BuildingType newheight) throws InvalidBuildException {

        if (newheight.compareTo(cell.getHeight()) <= 0) throw new InvalidBuildException();
        else if (newheight.compareTo(cell.getHeight()) >= 2) throw new InvalidBuildException();

    }

    /**
     * Resets player's state to WAIT.
     */
    void resetState() {
        this.getPlayer().setState(WAIT);
    }

    /**
     * This method checks the basic rules for moving, which are valid for every builder type
     *
     * @param finalPoint represents the cell to which the builder wants to move
     * @throws InvalidMoveException when the move is illegal
     * @throws ApolloException      --> {@link ApolloException}
     * @throws MinotaurException    --> {@link MinotaurException}
     * @throws ArtemisException     --> {@link ArtemisException}
     * @throws PanException         --> {@link PanException}
     */
    void isValidMove(Cell finalPoint) throws InvalidMoveException, ApolloException, ArtemisException, MinotaurException, PanException {

        if (finalPoint == null || finalPoint.getRow() < 0 || finalPoint.getRow() > 4 || finalPoint.getColumn() < 0 || finalPoint.getColumn() > 4)
            throw new InvalidMoveException(); //out of bounds
        else if (!(this instanceof Athena) && this.position.getGameTable().getAthenaMove() && finalPoint.getHeight().compareTo(position.getHeight()) >= 1)
            throw new InvalidMoveException(); // Athena moved up last turn, so this player can't go up
        else if (finalPoint.getHeight() == BuildingType.DOME) throw new InvalidMoveException(); // moving on dome
        else if (finalPoint.getHeight().compareTo(position.getHeight()) >= 2)
            throw new InvalidMoveException(); // check if I'm moving up more than one level
        else if (!(position.getNear().contains(finalPoint)))
            throw new InvalidMoveException(); // check that the cell I'm moving to is adjacent

    }

    /**
     * This method checks the postconditions for moving
     * @param finalPoint position to check
     * @throws InvalidMoveException in case postconditions are not met
     */
    protected void verifyMove(Cell finalPoint) throws InvalidMoveException {

        if (finalPoint.getBuilder() != null) throw new InvalidMoveException();

    }

    /**
     * @param position specifies position where to force-move the builder
     */
    public void forceMove(Cell position){
        position.setBuilder(this);
        this.position = position;
    }

    /**
     * Swaps two builders' positions.
     * @param firstBuilder builder which wants to swap position
     * @param otherBuilder builder whose position gets swapped
     */
    protected void swapPosition(Builder firstBuilder, Builder otherBuilder) {
        CellPointer temp = new CellPointer(firstBuilder.getPosition());
        firstBuilder.forceMove(otherBuilder.getPosition());
        otherBuilder.forceMove(temp.getPointer());
    }

    protected Player getPlayer() {
        return this.player;
    }

    /**
     * Checks whether the builder has any feasible move.
     * @return true if there's at least a valid move to be made.
     */
    boolean hasAvailableMoves() {
        return this.position.getNear().stream().anyMatch(c -> {
            try {
                isValidMove(c);
                return true;
            } catch (InvalidMoveException e) {
                return false;
            } catch (Exception e) {
                return true;
            }
        });
    }

    /**
     * Checks whether the builder has any feasible build.
     * @return true if there's at least a valid build to be made.
     */
    boolean hasAvailableBuilds() {
        return this.position.getNear().stream().anyMatch(c -> {
            try {
                isValidBuild(c, c.getHeight().getNext());
                return true;
            } catch (InvalidBuildException e) {
                return false;
            } catch (Exception e) {
                return true;
            }
        });
    }

    /**
     * @return State from which the player starts.
     */
    public ClientState getFirstState() {
        return ClientState.MOVE;
    }


}


/**
 * this class is just a commodity class whose job is
 * to keep a pointer to a Cell object
 */
class CellPointer {
    public Cell getPointer() {
        return pointer;
    }

    public CellPointer(Cell pointer) {
        this.pointer = pointer;
    }

    private final Cell pointer;

}