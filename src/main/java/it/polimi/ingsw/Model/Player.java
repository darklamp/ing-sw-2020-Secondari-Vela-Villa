package it.polimi.ingsw.Model;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.NickAlreadyTakenException;
import it.polimi.ingsw.Model.Exceptions.NoMoreMovesException;
import it.polimi.ingsw.Network.Messages.ServerMessage;
import it.polimi.ingsw.Network.SocketClientConnection;

import java.io.Serializable;
import java.util.ArrayList;

import static it.polimi.ingsw.Client.ClientState.*;

public class Player implements Serializable {

    private static final long serialVersionUID = 17756L;

    private final ArrayList<Builder> builderList; //array of builders

    private GameTable gameTable;
    private transient SocketClientConnection connection;

    private final String nickname; //private attribute for the Player's ID

    public String getGod() {
        return god;
    }

    private String god;

    private ClientState turnState;

    public boolean isFirstTime() {
        return firstTime;
    }

    public void setFirstTime(boolean firstTime) {
        this.firstTime = firstTime;
    }

    private boolean firstTime = true;

    /**
     * Constructor for player; once the object is built, it gets added to the player list in GameTable.
     *
     * @param nickname  player's nickname
     * @param gameTable game-specific table
     * @param c         player's socket connection
     * @throws NickAlreadyTakenException when nick already taken
     * @throws NullPointerException      when the gametable has not been initialized (shouldn't happen btw)
     */
    public Player(String nickname, GameTable gameTable, SocketClientConnection c) throws NickAlreadyTakenException {
        if (gameTable == null) throw new NullPointerException();
        else if (!gameTable.isValidPlayer(nickname)) throw new NickAlreadyTakenException();
        else {
            this.gameTable = gameTable;
            this.connection = c;
            this.nickname = nickname;  //If the nickname is accepted,the player'll be insert in the game
            this.builderList = new ArrayList<>();
            gameTable.addPlayer(this);
        }
    }

    /**
     * @param god
     * Assigned god to the player
     */
    public void setGod(Integer god){
        this.god = GameTable.getCompleteGodList().get(god);
        this.turnState = gameTable.getPlayerIndex(this) == 1 ? getFirstTurnState(god) : ClientState.WAIT;
    }

    /**
     * @param god of the player
     * @return clientstate "MoveOrBuild" if the god is Prometheus, otherwise just clientstate "move"
     */
    private ClientState getFirstTurnState(Integer god){
        return (god == 8) ? MOVEORBUILD : MOVE; /* if god is prometheus, he can move as well as build at first */
    }

    SocketClientConnection getConnection(){
        return this.connection;
    }

    /**
     * remove builders from the GameTable
     */
    void removeBuilders(){
        this.getBuilderList().get(0).getPosition().setBuilder(null);
        this.getBuilderList().get(1).getPosition().setBuilder(null);
    }

    /**
     * DEBUG package-private constructor only used in tests
     **/
    @Deprecated
    Player(String nickname, GameTable g, String god) throws NickAlreadyTakenException, NullPointerException {   //contructor method for player
        if (g == null) throw new NullPointerException();
        else if (!g.isValidPlayer(nickname)) throw new NickAlreadyTakenException();
            // else if (!isValidGod(god)) throw new InvalidGodException();
        else {
            this.nickname = nickname;  //If the nickname is accepted,the player'll be insert in the game
            this.god = god.toUpperCase();
            this.builderList = new ArrayList<>();
            g.addPlayer(this);
        }
    }

    public ClientState getState(){
        return this.turnState;
    }
    public void setState(ClientState state){
        this.turnState = state;
    }
    public ClientState getFirstState(){
        return this.builderList.get(0).getFirstState();
    }

    /**
     * This function gets called twice per player by the controller, to initialize the player's builders
     * @param position position on which the builder is to be placed
     * @throws InvalidBuildException when the builder is trying to be placed on an occupied cell or the player already has two builders
     */
    public void initBuilderList(Cell position) throws InvalidBuildException {
        if (position.getBuilder() != null || this.builderList.size() == 2) throw new InvalidBuildException();
        switch (this.god) {
            case "ATLAS" -> this.builderList.add(new Atlas(position, this));
            case "APOLLO" -> this.builderList.add(new Apollo(position, this));
            case "ARTEMIS" -> this.builderList.add(new Artemis(position, this));
            case "ATHENA" -> this.builderList.add(new Athena(position, this));
            case "DEMETER" -> this.builderList.add(new Demeter(position, this));
            case "HEPHAESTUS" -> this.builderList.add(new Hephaestus(position, this));
            case "MINOTAUR" -> this.builderList.add(new Minotaur(position, this));
            case "PAN" -> this.builderList.add(new Pan(position, this));
            case "PROMETEUS" -> this.builderList.add(new Prometheus(position, this));
            default -> throw new InvalidBuildException();
        }
    }

    /**
     * @return true if this player's {@link Player#builderList} is empty
     */
    public boolean hasNoBuilder() {
        return this.builderList.size() == 0;
    }

    /**
     * Checks if the player meets the necessary conditions to continue playing.
     *
     * @param builder builder to be checked (null if both builders need to be checked)
     * @throws NoMoreMovesException if the player does not meet playable conditions (aka he can't move/build)
     */
    void checkConditions(Builder builder) throws NoMoreMovesException {
        switch (turnState) {
            case MOVE -> checkMovePreConditions(builder);
            case BUILD -> checkBuildPreConditions(builder);
            case MOVEORBUILD -> {
                try {
                    checkMovePreConditions(builder);
                } catch (NoMoreMovesException e) {
                    turnState = BUILD;
                }
                try {
                    checkBuildPreConditions(builder);
                } catch (NoMoreMovesException e) {
                    if (turnState == BUILD) throw e;
                    else turnState = MOVE;
                }
            }
            case BUILDORPASS -> {
                try {
                    checkBuildPreConditions(builder);
                } catch (NoMoreMovesException e) {
                    gameTable.nextTurn();
                }
            }
            default -> {
            }
        }
    }

    /**
     * Checks for available feasible moves
     *
     * @see NoMoreMovesException
     */
    private void checkMovePreConditions(Builder builder) throws NoMoreMovesException {
        int builders = 1;
        boolean b1 = firstTime;
        ArrayList<Builder> builderList = new ArrayList<>();
        if (builder == null) { //player is building before moving: must check both builders
            builders = 2;
            builderList = this.builderList;
        } else {
            builderList.add(builder);
        }
        for (Builder b : builderList) {
            if (!b.hasAvailableMoves()) builders -= 1;
            if (b1) firstTime = true;
        }
        if (builders == 0) throw new NoMoreMovesException(this);
        if (b1) firstTime = true;

    }

    /**
     * Checks for available feasible builds
     *
     * @see NoMoreMovesException
     */
    private void checkBuildPreConditions(Builder builder) throws NoMoreMovesException {
        int builders = 1;
        boolean b1 = firstTime;
        ArrayList<Builder> builderList = new ArrayList<>();
        if (builder == null) { //player is building before moving: must check both builders
            builders = 2;
            builderList = this.builderList;
        } else {
            builderList.add(builder);
        }
        for (Builder b : builderList) {
            if (!b.hasAvailableBuilds()) builders -= 1;
            if (b1) firstTime = true;
        }
        if (builders == 0) throw new NoMoreMovesException(this);
        if (b1) firstTime = true;
    }

    ArrayList<Builder> getBuilderList() {
        return this.builderList;
    }

    public String getNickname() {
        return nickname;
    }

    public void setConnection(SocketClientConnection connection) {
        this.connection = connection;
    }

    synchronized void kick(int pIndex) {
        this.connection.setGracefulClose();
        gameTable.setNews(new News(ServerMessage.connClosed + "@@@" + pIndex, null), "PLAYERKICKED");
        this.connection.closeConnection();
    }
}
