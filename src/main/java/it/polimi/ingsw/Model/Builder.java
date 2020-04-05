package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;

import java.io.Serializable;

import static it.polimi.ingsw.Model.TurnState.MOVE;

public abstract class Builder implements Serializable {

    private Cell position;

    private final Player player;

    public Builder(Cell position, Player player) { // constructor for Builder with Cell parameter
        if (player == null || position == null) throw new NullPointerException();
        else {
            this.player = player;
            this.position = position;
            position.setBuilder(this);
        }
    }

    /**
     * @return position of given builder, in form of cell
     */
    public Cell getPosition() {
        return position;
    }

    /**
     * @param position position to move the builder to
     * @throws InvalidMoveException if the move isn't allowed
     */
    public void setPosition(Cell position) throws InvalidMoveException, ArtemisException, PanException, PrometeusException {
        try {
            isValidMove(position); // check validity of move
            this.position.setBuilder(null);
            this.position = position; // sets position if no exceptions are thrown
        } catch (NullPointerException e) {
            e.printStackTrace(); // unhandled error
        }
        catch (ApolloException e) { // swap position cause it's Apollo TODO: do not make someone win if they win by being forced to move
            swapPosition(this,position.getBuilder());
            //todo?
        }
        catch (MinotaurException e) { // change position cause it's Minotaurrr TODO: do not make someone win if they win by being forced to move
            //todo
        }
        catch (ArtemisException e){
            throw new ArtemisException(); // TEMP PER TEST
        }
    }

    /**
     * @param cell represents the cell on which the builder wants to build
     * @param newheight represents "new height", meaning the height which the builder wants to build on
     * @throws AtlasException see Atlas
     * @throws InvalidBuildException when the build is illegal
     */
    protected void isValidBuild(Cell cell, BuildingType newheight) throws InvalidBuildException,AtlasException, HephaestusException, DemeterException {

        if (cell.getHeight().equals(BuildingType.DOME)) throw new InvalidBuildException(); // verify that there is no dome on the cell
        else if (cell.getHeight().equals(BuildingType.TOP) && !(newheight.equals(BuildingType.DOME))) throw new InvalidBuildException(); // if a top level is present, only a dome can be placed

    }

    /**
     * this method check postconditions for building
     * @param cell represents the cell on which the builder wants to build
     * @param newheight represents "new height", meaning the height which the builder wants to build on
     * @throws InvalidBuildException when the build is illegal
     */
    protected void verifyBuild(Cell cell, BuildingType newheight) throws InvalidBuildException {

        if (newheight.compareTo(cell.getHeight()) <= 0)  throw new InvalidBuildException();
        else if (newheight.compareTo(cell.getHeight()) >= 2) throw new InvalidBuildException();

    }

    void resetState(){
        this.getPlayer().setState(MOVE);
    }

    /**
     * this method checks the basic rules for moving, which are valid for every builder type
     * @param finalPoint represents the cell to which the builder wants to move
     * @throws InvalidMoveException when the move is illegal
     * @throws ApolloException when Apollo TODO better javadoc
     * @throws MinotaurException TODO better javadoc
     */
    void isValidMove(Cell finalPoint) throws InvalidMoveException, ApolloException, ArtemisException, MinotaurException, PrometeusException, PanException {

        if(finalPoint == null || finalPoint.getX() < 0 || finalPoint.getX() > 4 || finalPoint.getY() < 0 || finalPoint.getY() > 4) throw new InvalidMoveException(); //out of bounds
        else if (GameTable.getAthenaMove() && finalPoint.getHeight().compareTo(position.getHeight()) >= 1) throw new InvalidMoveException(); // Athena moved up last turn, so this player can't go up
        else if (finalPoint.getHeight() == BuildingType.DOME) throw new InvalidMoveException(); // moving on dome
        else if (finalPoint.getHeight().compareTo(position.getHeight()) >= 2) throw new InvalidMoveException(); // check if I'm moving up more than one level
        else if (!(position.getNear().contains(finalPoint))) throw new InvalidMoveException(); // check that the cell I'm moving to is adjacent

    }

    /**
     * this method checks the postconditions for moving
     * @param finalPoint position to check
     * @throws InvalidMoveException in case postconditions are not met
     */
    protected void verifyMove(Cell finalPoint) throws InvalidMoveException {

        if (finalPoint.getBuilder() != null) throw new InvalidMoveException();

    }

    /**
     * @param position specifies position where to force-move the builder
     */
    private void forceMove(Cell position){
        position.setBuilder(this);
        this.position = position;
    }

    /**
     * @param firstBuilder builder which wants to swap position
     * @param otherBuilder builder whose position gets swapped
     */
    protected void swapPosition(Builder firstBuilder, Builder otherBuilder) { // TODO verificare che funzioni
        CellPointer temp = new CellPointer(firstBuilder.getPosition());
        firstBuilder.forceMove(otherBuilder.getPosition());
        otherBuilder.forceMove(temp.getPointer());
    }

    /**
     * getter for player
     */
    protected Player getPlayer(){
        return this.player;
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

    public void setPointer(Cell pointer) {
        this.pointer = pointer;
    }

    public CellPointer(Cell pointer) {
        this.pointer = pointer;
    }

    private Cell pointer;

}