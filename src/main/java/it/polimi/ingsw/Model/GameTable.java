package it.polimi.ingsw.Model;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;
import it.polimi.ingsw.Model.Exceptions.NoMoreMovesException;
import it.polimi.ingsw.Network.GameStateMessage;
import it.polimi.ingsw.Network.Server;
import it.polimi.ingsw.Network.SocketClientConnection;
import it.polimi.ingsw.View.CellView;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class GameTable implements Serializable {

    private static final long serialVersionUID = 17756L;

    /**
     * 5x5 matrix representing game table
     **/
    private final Cell[][] Table;

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

    public static final List<String> completeGodList = Arrays.asList("APOLLO", "ARTEMIS", "ATHENA", "ATLAS", "DEMETER", "HEPHAESTUS", "MINOTAUR", "PAN", "PROMETEUS"); /* list containing all the basic gods */

    /**
     * variable which holds the current builder being used by the player
     **/
    private Builder currentBuilder = null;

    /**
     * support boolean for Athena
     **/
    private boolean athenaMove = false;


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
     */
    public void nextTurn() throws NoMoreMovesException {
        getCurrentPlayer().setFirstTime(true);
        getCurrentBuilder().resetState();
        setCurrentBuilder(null);
        if (currentPlayer == players.size() - 1) currentPlayer = 0;
        else currentPlayer++;
        try {
            if (getCurrentPlayer().getBuilderList().get(0).getFirstState() != ClientState.MOVEORBUILD)
                checkMovePreConditions();
        } finally {
            getCurrentPlayer().setState(getCurrentPlayer().getBuilderList().get(0).getFirstState());
            resetMoveTimer();
        }
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
                        sleep(TimeUnit.MINUTES.toMillis(Server.getMoveTimer()));
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
     */
    public int getPlayerIndex(Player player) {
        if (players.get(0).equals(player)) return 0;
        else if (players.get(1).equals(player)) return 1;
        else return playersNumber == 3 ? 2 : -1; // -1 is error case
    }

    /**
     * Checks for available feasible moves
     * @see NoMoreMovesException
     */
    public final void checkMovePreConditions() throws NoMoreMovesException {
        ArrayList<Builder> builderList = players.get(currentPlayer).getBuilderList();
        boolean b1 = players.get(currentPlayer).isFirstTime();
        int movableBuilders = 1;
        if (currentBuilder == null) {
            movableBuilders = 2;
            for (Builder b : builderList) {
                if (!b.hasAvailableMoves()) movableBuilders -= 1;
                if (b1) players.get(currentPlayer).setFirstTime(true);
            }
        } else {
            if (!currentBuilder.hasAvailableMoves()) movableBuilders -= 1;
            if (b1) players.get(currentPlayer).setFirstTime(true);
        }
        if (movableBuilders == 0) throw new NoMoreMovesException(players.get(currentPlayer));
        if (b1) players.get(currentPlayer).setFirstTime(true);

    }

    /**
     * Checks for available feasible moves
     *
     * @see NoMoreMovesException
     */
    public final void checkBuildPreConditions(Builder builder) throws NoMoreMovesException {
        int builders = 1;
        boolean b1 = players.get(currentPlayer).isFirstTime();
        ArrayList<Builder> builderList = new ArrayList<>();
        if (builder == null) { //player is building before moving: must check both builders
            builders = 2;
            builderList = players.get(currentPlayer).getBuilderList();
        } else {
            builderList.add(builder);
        }

        for (Builder b : builderList) {
            if (!b.hasAvailableBuilds()) builders -= 1;
            if (b1) players.get(currentPlayer).setFirstTime(true);
        }
        if (builders == 0) throw new NoMoreMovesException(players.get(currentPlayer));
        if (b1) players.get(currentPlayer).setFirstTime(true);
    }

    /**
     * Handles player removal
     *
     * @param player to be removed
     */
    public synchronized void removePlayer(Player player, boolean checkWinner) throws NoMoreMovesException {
        int pIndex = getPlayerIndex(player);
        player.removeBuilders();
        this.players.remove(player);
        player.kick(pIndex);
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
                    checkMovePreConditions();
                } finally {
                    getCurrentPlayer().setState(getCurrentPlayer().getBuilderList().get(0).getFirstState());
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
        for (Player p : players) {
            try {
                removePlayer(p, false);
            } catch (NoMoreMovesException ignored) {
            }
        }
    }


    /* start observable pattern objects */

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    /**
     * Listener helper object
     **/
    private transient News news;

    /**
     * Listener news
     **/

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void setNews(News news, String type) {
        support.firePropertyChange(type, this.news, news);
        this.news = news;
        this.type = type;
    }

    /* end observable pattern objects */


    /* end observable pattern */

    /**
     * @return 5x5 matrix containing CellViews, to be sent to clients
     */
    public CellView[][] getBoardCopy() {
        CellView[][] out = new CellView[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++){
                out[i][j] = Table[i][j].getModelView(this);
            }
        }
        return out;
    }

    /**
     * Constructor for GameTable. Takes number of players as argument.
     */
    public GameTable(int playersNumber) {   //contructor method for GameTable

        Table = new Cell[5][5]; //create new Table
        arrayTable = new ArrayList<>();
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 5; j++){
                Table[i][j] = new Cell(i,j,this);
                arrayTable.add(Table[i][j]);
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
     * @return Cell on which the first builder connected to the passed SocketClientConnection stands
     */
    public Cell getCell(SocketClientConnection c) {
        return c.getPlayer().getBuilderList().get(0).getPosition();
    }

    /**
     * @param nickname contains nickname to be checked
     * @return true if the nickname isn't already in use, false otherwise
     */
    /**
     * support boolean for Athena
     **/
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean isValidPlayer(String nickname) {
        if (players == null) {
            players = new ArrayList<Player>();
            return true;
        }
        return players.stream().noneMatch(player -> sameNickname(player, nickname));
    }


    /* start simple getters / setters */


    public Cell getCell(int x, int y) throws InvalidCoordinateException {
        isInvalidCoordinate(x, y);
        return Table[x][y];
    }

    /**
     * @return true if player's nickname is the same as nickname
     */
    private boolean sameNickname(Player player, String nickname) {
        return player.getNickname().equals(nickname);
    }

    void addPlayer(Player player) {
        players.add(player);
    }

    ArrayList<Cell> toArrayList() {
        return arrayTable;
    }

    void setAthenaMove(boolean newValue) {
        athenaMove = newValue;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    boolean getAthenaMove() {
        return athenaMove;
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
        ArrayList<SocketClientConnection> out = new ArrayList<>();
        for (Player p : players) {
            out.add(p.getConnection());
        }
        return out;
    }

    public Builder getCurrentBuilder() {
        return currentBuilder;
    }

    public void setCurrentBuilder(Builder currentBuilder) {
        this.currentBuilder = currentBuilder;
    }

    public ArrayList<Player> getPlayers() {
        return players; }
    /* end simple getters / setters */


    /**
     * Only used for tests.
     */
    private void setCurrentPlayer(int player) {
        this.currentPlayer = player;
    }

    private String type;


}
