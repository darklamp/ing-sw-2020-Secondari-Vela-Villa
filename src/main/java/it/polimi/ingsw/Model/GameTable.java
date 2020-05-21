package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;
import it.polimi.ingsw.Model.Exceptions.NoMoreMovesException;
import it.polimi.ingsw.Network.GameStateMessage;
import it.polimi.ingsw.Network.Server;
import it.polimi.ingsw.Network.SocketClientConnection;
import it.polimi.ingsw.View.CellView;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class GameTable {

    private Thread timerThread = null;

    private final Cell[][] Table;
    /**
     * 5x5 matrix representing game table
     **/
    private final int gameIndex;
    /**
     * index of current game
     **/
    private ArrayList<Player> players;
    /**
     * arraylist filled with players
     **/
    private final ArrayList<Cell> arrayTable;
    /**
     * simple object which contains the 25 pairs of coordinates from 0,0 to 4,4 as an arraylist of pair objects
     */
    private int currentPlayer = 1;
    /**
     * current player index
     **/
    private final int playersNumber;
    /**
     * number of players in game
     **/
    public static final List<String> completeGodList = Arrays.asList("APOLLO", "ARTEMIS", "ATHENA", "ATLAS", "DEMETER", "HEPHAESTUS", "MINOTAUR", "PAN", "PROMETEUS"); /* list containing all the basic gods */
    private ArrayList<String> godChoices;
    /**
     * list of gods chosen by the first player to be available in the game
     **/
    private Builder currentBuilder = null;
    /**
     * variable which holds the current builder being used by the player
     **/
    private static boolean athenaMove = false;

    /**
     * support boolean for Athena
     **/

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
        if (currentPlayer == playersNumber - 1) currentPlayer = 0;
        else currentPlayer++;
        checkMovePreConditions();
        getCurrentPlayer().setState(getCurrentPlayer().getBuilderList().get(0).getFirstState());
        resetMoveTimer();
    }


    private volatile boolean exit = false;

    /**
     * When called, this method starts a timer of length {@link Server#getMoveTimer()} (using minutes as timeunit).
     * If this function gets called while the timer is still running, it interrupts the timer thread, so as to reset the timer.
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
                System.err.println("Player " + players.get(currentPlayer) + " timed out. Closing game...");
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
        int movableBuilders = 2;
        for (Builder b : builderList){
            if (!b.hasAvailableMoves()) movableBuilders -= 1;
        }
        if (movableBuilders == 0)  throw new NoMoreMovesException(players.get(currentPlayer));
    }

    /**
     * Handles player removing after win / loss
     * @param player to be removed
     */
    public void removePlayer(Player player){
        this.players.remove(player);
        player.removeBuilders();
        player.kick();
        //TODO handle new player number etc..
    }

    /**
     * Clears up game instance.
     */
    public void closeGame() {
        for (Player p : players) {
            removePlayer(p);
        }
    }


    /* start observable pattern objects */

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    /**
     * Listener helper object
     **/
    private News news;

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
    private static void isInvalidCoordinate(int x, int y) throws InvalidCoordinateException {
        if (x > 4 || x < 0 || y > 4 || y < 0) {
            throw new InvalidCoordinateException();
        }
    }

    /**
     * @return Cell on which the first builder connected to the passed SocketClientConnection stands
     */
    public Cell getCell(SocketClientConnection c){
        return c.getPlayer().getBuilderList().get(0).getPosition();
    }

    /**
     * @param nickname contains nickname to be checked
     * @return true if the nickname isn't already in use, false otherwise
     */
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

    static void setAthenaMove(boolean newValue) {
        athenaMove = newValue;
    }

    public void setGods(ArrayList<Integer> godChoices) {
        ArrayList<String> gods = new ArrayList<>();
        for (Integer godChoice : godChoices) {
            gods.add(completeGodList.get(godChoice));
        }
        this.godChoices = gods;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    static boolean getAthenaMove() {
        return athenaMove;
    }

    public static List<String> getCompleteGodList() {
        return completeGodList;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayer);
    }

    public GameStateMessage getGameState() {
        return new GameStateMessage(players.get(0).getState(), players.get(1).getState(), playersNumber == 3 ? players.get(2).getState() : null);
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


    /* end simple getters / setters */


    /**
     * Only used for tests.
     */
    private void setCurrentPlayer(int player) {
        this.currentPlayer = player;
    }

    private String type;


}
