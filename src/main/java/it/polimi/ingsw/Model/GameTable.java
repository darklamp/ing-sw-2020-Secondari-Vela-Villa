package it.polimi.ingsw.Model;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Controller.Exceptions.IllegalTurnStateException;
import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;
import it.polimi.ingsw.Model.Exceptions.NoMoreMovesException;
import it.polimi.ingsw.Network.Messages.GameStateMessage;
import it.polimi.ingsw.Network.Server;
import it.polimi.ingsw.Network.SocketClientConnection;
import it.polimi.ingsw.Utility.Pair;
import it.polimi.ingsw.View.CellView;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static it.polimi.ingsw.Client.ClientState.*;
import static java.lang.Thread.sleep;

public class GameTable implements Serializable {

    private static final long serialVersionUID = 17756L;

    /**
     * 5x5 matrix representing game table
     **/
    private final Cell[][] table;

    /**
     * index of current game
     **/
    private final int gameIndex;

    /**
     * arraylist filled with players
     **/
    private ArrayList<Player> players;

    /**
     * simple object which contains the 25 pairs of coordinates from 0,0 to 4,4 as an arraylist of pair objects
     */
    private final ArrayList<Cell> arrayTable;

    /**
     * current player index
     **/
    private int currentPlayer = 1;

    /**
     * number of players in game
     **/
    private int playersNumber;

    public static List<String> completeGodList = new ArrayList<>(Arrays.asList("APOLLO", "ARTEMIS", "ATHENA", "ATLAS", "DEMETER", "HEPHAESTUS", "MINOTAUR", "PAN", "PROMETHEUS"));
    ;

    /**
     * variable which holds the current builder being used by the player
     **/
    private Builder currentBuilder = null;


    private volatile boolean exit = false;
    private transient Thread timerThread = null;

    /**
     * The purpose of this function is going to the next turn;
     * specifically, what this function does is:
     * - reset the boolean firstTime in currentPlayer to true
     * - reset the currentBuilder attribute to a default null value
     * - increase the currentPlayer index, so as to effectively skip to next player
     * - check for preConditions -> if the player can't move, throw
     * - change client state
     *
     * @throws NoMoreMovesException if the player doesn't pass new turn checks
     */
    public void nextTurn() throws NoMoreMovesException {
        getCurrentPlayer().setFirstTime(true);
        getCurrentBuilder().resetState();
        setCurrentBuilder(null);
        if (currentPlayer == players.size() - 1) currentPlayer = 0;
        else currentPlayer++;
        getCurrentPlayer().setState(getCurrentPlayer().getBuilderList().get(0).getFirstState());
        try {
            getCurrentPlayer().checkConditions(null);
            setNews(new News(), "TURN");
        } finally {
            resetMoveTimer();
        }
    }

    /**
     * Checks whether the player is in a legal state for the move it wants to make.
     *
     * @param toCheck ClientState to be checked
     * @param player  player that wants to move
     * @throws IllegalTurnStateException if not matching
     */
    void isLegalState(ClientState toCheck, Player player) throws IllegalTurnStateException {
        if (getCurrentPlayer() != player) throw new IllegalTurnStateException();
        ClientState state = getCurrentPlayer().getState();
        switch (toCheck) {
            case BUILD -> {
                if (state != BUILD && state != BUILDORPASS && state != MOVEORBUILD)
                    throw new IllegalTurnStateException();
            }
            case MOVE -> {
                if (state != MOVE && state != MOVEORBUILD) throw new IllegalTurnStateException();
            }
            default -> throw new IllegalTurnStateException();
        }
    }

    /**
     * Checks if the player meets the necessary conditions to continue playing.
     *
     * @throws NoMoreMovesException if the player does not meet playable conditions (aka he can't move/build)
     */
    public void checkConditions() throws NoMoreMovesException {
        getCurrentPlayer().checkConditions(currentBuilder);
    }


    /**
     * When called, this method starts a timer of length {@link Server#getMoveTimer()} (using minutes as timeunit).
     * If this function gets called while the timer is still running, it interrupts the {@link #timerThread}, so as to reset the timer.
     * If the timer runs out, the thread exits the loop by setting {@link #exit} to true, and manages the game ending.
     */
    public void resetMoveTimer() {
        if (timerThread != null) {
            timerThread.interrupt();
        } else {
            timerThread = new Thread(() -> {
                while (!exit) {
                    try {
                        //noinspection BusyWait
                        sleep(Server.getMoveTimer());
                        exit = true;
                    } catch (InterruptedException ignored) {
                    }
                }
                System.err.println("Player timed out. Closing game...");
                setNews(new News(), "ABORT");
            });
            timerThread.start();
        }
    }

    /**
     * Takes a Player argument and determines its "player number", aka its index in the players list.
     * @param player player whose index the function returns
     * @return index of player (0..2)
     */
    public int getPlayerIndex(Player player) {
        if (players.get(0).equals(player)) return 0;
        else if (players.get(1).equals(player)) return 1;
        else return (playersNumber == 3 && players.get(2).equals(player)) ? 2 : -1; // -1 is error case
    }


    /**
     * Handles player removal
     * @param player to be removed
     * @param checkWinner boolean to tell the function whether or not it should perform additional checks
     * @throws NoMoreMovesException if, after the passed player gets removed, the next player doesn't pass checks
     */
    public synchronized void removePlayer(Player player, boolean checkWinner) throws NoMoreMovesException {
        int pIndex = getPlayerIndex(player);
        player.removeBuilders();
        this.players.remove(player);
        player.kick(pIndex, !checkWinner);
        if (checkWinner && players.size() == 1) {
            players.get(0).setState(ClientState.WIN);
            setNews(new News(null, players.get(0).getConnection()), "WIN");
        } else if (checkWinner) {
            playersNumber = 2;
            if (currentPlayer == pIndex) {
                if (currentPlayer == 2) {
                    currentPlayer = 0;
                }
                getCurrentPlayer().setFirstTime(true);
                setCurrentBuilder(null);
                try {
                    getCurrentPlayer().setState(getCurrentPlayer().getBuilderList().get(0).getFirstState());
                    checkConditions();
                } finally {
                    resetMoveTimer();
                }
            }
            setNews(new News(null, null), "TURN");
        }
    }

    /**
     * Clears up game instance.
     */
    synchronized public void closeGame() {
        Player p = players.get(0);
        try {
            while (true) {
                try {
                    removePlayer(p, false);
                } catch (NoMoreMovesException ignored) {
                } finally {
                    p = players.get(0);
                }
            }
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    /**
     * Listener helper object
     **/
    private transient News news;

    /**
     * Adds a listener to this instance of GameTable.
     * @param pcl PropertyChangeListener to be added
     **/
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void setNews(News news, String type) {
        support.firePropertyChange(type, this.news, news);
        this.news = news;
        this.type = type;
    }

    /**
     * @return 5x5 matrix containing CellViews, to be sent to clients
     */
    public CellView[][] getBoardCopy() {
        CellView[][] out = new CellView[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++){
                out[i][j] = table[i][j].getModelView();
            }
        }
        return out;
    }

    /**
     * Constructor for GameTable.
     *
     * @param playersNumber number of players in game
     */
    public GameTable(int playersNumber) {

        table = new Cell[5][5];
        arrayTable = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                table[i][j] = new Cell(i, j, this);
                arrayTable.add(table[i][j]);
            }
        }
        this.gameIndex = Server.getCurrentGameIndex();
        players = null;
        this.playersNumber = playersNumber;

    }

    /**
     * @param x X coord
     * @param y Y coord
     * @throws InvalidCoordinateException if coordinate is OOB
     */
    public static void isInvalidCoordinate(int x, int y) throws InvalidCoordinateException {
        if (x > 4 || x < 0 || y > 4 || y < 0) {
            throw new InvalidCoordinateException();
        }
    }

    /**
     * @param nickname contains nickname to be checked
     * @return true if the nickname isn't already in use, false otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean isValidPlayer(String nickname) {
        if (players == null) {
            players = new ArrayList<>();
            return true;
        }
        return players.stream().noneMatch(player -> player.getNickname().equals(nickname));
    }

    /**
     * Getter for table's cells.
     *
     * @param x x coordinate of the cell
     * @param y y coordinate of the cell
     * @return Cell with the given coordinates
     * @throws InvalidCoordinateException if the given coordinates are invalid
     */
    public Cell getCell(int x, int y) throws InvalidCoordinateException {
        isInvalidCoordinate(x, y);
        return table[x][y];
    }

    /**
     * Getter for table's cells.
     *
     * @param p pair of coordinates
     * @return Cell with the given coordinates
     * @throws InvalidCoordinateException if the given coordinates are invalid
     */
    public Cell getCell(Pair p) throws InvalidCoordinateException {
        if (p == null) throw new InvalidCoordinateException();
        return getCell(p.getFirst(), p.getSecond());
    }

    /**
     * Adds player to the table
     *
     * @param player player to be added
     */
    void addPlayer(Player player) {
        players.add(player);
    }

    /**
     * @return arraylist representation of the table
     */
    ArrayList<Cell> toArrayList() {
        return arrayTable;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public static List<String> getCompleteGodList() {
        return completeGodList;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayer);
    }

    public int getGameIndex() {
        return gameIndex;
    }

    public GameStateMessage getGameState() {
        return new GameStateMessage(players.get(0).getState(), players.size() == 1 ? ClientState.LOSE : players.get(1).getState(), players.size() == 3 ? players.get(2).getState() : null, players.get(0).getNickname(), players.size() == 1 ? null : players.get(1).getNickname(), players.size() == 3 ? players.get(2).getNickname() : null, currentPlayer);
    }

    public ArrayList<SocketClientConnection> getPlayerConnections() {
        return players.stream().map(Player::getConnection).collect(Collectors.toCollection(ArrayList::new));
    }

    public Builder getCurrentBuilder() {
        return currentBuilder;
    }

    public void setCurrentBuilder(Builder currentBuilder) {
        this.currentBuilder = currentBuilder;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Only used for tests.
     */
    private String type;
}
