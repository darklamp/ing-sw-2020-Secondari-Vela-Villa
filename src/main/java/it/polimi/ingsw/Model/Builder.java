package it.polimi.ingsw.Model;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;
import it.polimi.ingsw.Model.Exceptions.WinnerException;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static it.polimi.ingsw.Client.ClientState.*;


/**
 * Every class extending this AND annotated with @God is a god implementation.
 */
public abstract class Builder implements Serializable {

    private static final long serialVersionUID = 17756L;

    private Cell position;

    private final Player player;

    private boolean first;

    public Builder(Cell position, Player player) { // constructor for Builder with Cell parameter
        if (player == null || position == null) throw new NullPointerException();
        else {
            this.player = player;
            this.position = position;
            this.first = !(player.hasBuilder());
            position.setBuilder(this);
        }
    }

    private GameTable getGameTable() {
        return this.position.getGameTable();
    }

    /**
     * @return true if the builder is the "first" as in first in the {@link Player#getBuilderList()}-returned list
     */
    public boolean isFirst() {
        return first;
    }

    /**
     * @return Cell on which the builder currently is
     */
    public Cell getPosition() {
        return position;
    }

    /**
     * Sets position of builder to a given cell.
     * Before executing, checks for preconditions.
     *
     * @param position position to move the builder to
     * @throws InvalidMoveException if the move isn't allowed / it's straight up wrong
     * @throws WinnerException      if the move led to a win
     */
    public void setPosition(Cell position) throws InvalidMoveException, WinnerException {
        ClientState nextState;
        News news = new News();
        try {
            getGameTable().isLegalState(MOVE, this.player);
        } catch (Exception e) {
            news.setRecipients(this.getPlayer());
            getGameTable().setNews(news, "MOVEKO");
            throw new InvalidMoveException();
        }
        try {
            isValidMove(position);
            nextState = this.executeMove(position);
        } catch (InvalidMoveException e) {
            news.setRecipients(this.player);
            getGameTable().setNews(news, "MOVEKO");
            throw e;
        }
        if (nextState == WIN || isWinner()) throw new WinnerException(this.player);
        this.player.setState(nextState);
        getGameTable().setCurrentBuilder(this);
        getGameTable().setNews(news, "MOVEOK");
    }

    /**
     * Very simply checks whether, after the last valid move, the builder moved to a TOP level.
     *
     * @return true if the client moved to a top level (hence it's the winner)
     */
    protected boolean isWinner() {
        return this.position.getHeight() == BuildingType.TOP;
    }

    /**
     * This method checks for the validity of a build move.
     *
     * @param cell      represents the cell on which the builder wants to build
     * @param newheight represents "new height", meaning the height which the builder wants to build on
     * @return next state
     * @throws InvalidBuildException when the build is illegal
     */
    protected ClientState isValidBuild(Cell cell, BuildingType newheight) throws InvalidBuildException {

        if (!(this.getPosition().getNear().contains((cell))))
            throw new InvalidBuildException(); // trying to build on a non-near cell
        else if (cell.getHeight().equals(BuildingType.DOME) || cell.getBuilder() != null)
            throw new InvalidBuildException(); // verify that there is no dome on the cell
        else if (cell.getHeight().equals(BuildingType.TOP) && !(newheight.equals(BuildingType.DOME)))
            throw new InvalidBuildException(); // if a top level is present, only a dome can be placed

        checkBuildHandicaps(cell, newheight);

        return WAIT;
    }

    /**
     * This function executes the move. The default implementation can be changed by the gods.
     *
     * @param position position to which the builder is to be moved
     * @return next state of the player
     */
    protected ClientState executeMove(Cell position) {
        this.position.setBuilder(null);
        this.position = position;
        position.setBuilder(this);
        return BUILD;
    }

    /**
     * This method checks postconditions for building
     *
     * @param cell      represents the cell on which the builder wants to build
     * @param newheight represents "new height", meaning the height which the builder wants to build on
     * @throws InvalidBuildException when the build is illegal
     */
    protected void verifyBuild(Cell cell, BuildingType newheight) throws InvalidBuildException {

        if (newheight.compareTo(cell.getHeight()) != 1) throw new InvalidBuildException();

    }

    /**
     * Resets player's state to WAIT.
     */
    protected void resetState() {
        this.getPlayer().setState(WAIT);
    }

    /**
     * This method is useful for those gods who have a "previous" attribute, like
     * {@link Demeter}. It has to be extended by the god so that it clears said attribute.
     */
    protected void clearPrevious() {
    }

    /**
     * This method checks the basic rules for moving, which are valid for every builder type
     *
     * @param finalPoint represents the cell to which the builder wants to move
     * @throws InvalidMoveException when the move is illegal
     */
    protected void isValidMove(Cell finalPoint) throws InvalidMoveException {

        if (finalPoint == null || finalPoint.getRow() < 0 || finalPoint.getRow() > 4 || finalPoint.getColumn() < 0 || finalPoint.getColumn() > 4)
            throw new InvalidMoveException(); //out of bounds
        else if (finalPoint.getHeight() == BuildingType.DOME) throw new InvalidMoveException(); // moving on dome
        else if (finalPoint.getHeight().compareTo(position.getHeight()) >= 2)
            throw new InvalidMoveException(); // check if I'm moving up more than one level
        else if (!(position.getNear().contains(finalPoint)))
            throw new InvalidMoveException(); // check that the cell I'm moving to is adjacent

        checkMoveHandicaps(finalPoint);
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
    void forceMove(Cell position) {
        position.setBuilder(this);
        this.position = position;
    }

    private final Predicate<Builder> excludeThisPlayer = b -> b != (this.isFirst() ? this : getPlayer().getBuilderList().get(0));
    private final Function<Player, Builder> playersToBuilder = p -> p.getBuilderList().get(0);

    private Stream<Player> getPlayersAsStream() {
        return getGameTable().getPlayers().stream();
    }

    /**
     * Checks all the handicaps for the move
     *
     * @param finalPoint final point of the move
     * @throws InvalidMoveException if an moveHandicap makes the move invalid
     */
    private void checkMoveHandicaps(Cell finalPoint) throws InvalidMoveException {
        if (getPlayersAsStream().map(playersToBuilder).filter(excludeThisPlayer).anyMatch(god -> god.moveHandicap(finalPoint, this.getPosition())))
            throw new InvalidMoveException();
    }

    /**
     * Checks all the handicaps for the build
     *
     * @param cell   final point of the build
     * @param height height of the building
     * @throws InvalidBuildException if an buildHandicap makes the move invalid
     */
    private void checkBuildHandicaps(Cell cell, BuildingType height) throws InvalidBuildException {
        if (getPlayersAsStream().map(playersToBuilder).filter(excludeThisPlayer).anyMatch(god -> god.buildHandicap(cell, height)))
            throw new InvalidBuildException();
    }

    /**
     * This method gets overridden by those gods that need to implement a buildHandicap to others.
     *
     * @param cell   final point of the build
     * @param height height of the building
     * @return true if the buildHandicap matches ( --> the build can't be done ), false otherwise
     */
    protected boolean buildHandicap(Cell cell, BuildingType height) {
        return false;
    }

    /**
     * This method gets overridden by those gods that need to implement a moveHandicap to others.
     *
     * @param finalPoint    final point of the move
     * @param startingPoint starting point of the move
     * @return true if the moveHandicap matches ( --> the move can't be done ), false otherwise
     */
    protected boolean moveHandicap(Cell finalPoint, Cell startingPoint) {
        return false;
    }

    /**
     * Swaps two builders' positions.
     *
     * @param firstBuilder builder which wants to swap position
     * @param otherBuilder builder whose position gets swapped
     */
    protected void swapPosition(Builder firstBuilder, Builder otherBuilder) {
        CellPointer temp = new CellPointer(firstBuilder.getPosition());
        firstBuilder.forceMove(otherBuilder.getPosition());
        otherBuilder.forceMove(temp.getPointer());
    }

    /**
     * @return the builder's associated player
     */
    protected Player getPlayer() {
        return this.player;
    }

    /**
     * Checks whether the builder has any feasible move.
     *
     * @return true if there's at least a valid move to be made.
     */
    boolean hasAvailableMoves() {
        return this.position.getNear().stream().anyMatch(c -> {
            try {
                isValidMove(c);
                return true;
            } catch (InvalidMoveException e) {
                return false;
            }
        });
    }

    /**
     * Checks whether the builder has any feasible build.
     * @return true if there's at least a valid build to be made.
     */
    boolean hasAvailableBuilds() {
        AtomicReference<BuildingType> b = new AtomicReference<>();
        return this.position.getNear().stream().anyMatch(c -> {
            b.set(c.getHeight());
            do {
                try {
                    isValidBuild(c, b.get().getNext());
                    return true;
                } catch (InvalidBuildException | IllegalArgumentException ignored) {
                }
                try {
                    b.set(b.get().getNext());
                } catch (IllegalArgumentException e) {
                    return false;
                }
            } while (b.get() != BuildingType.DOME);
            return false;
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