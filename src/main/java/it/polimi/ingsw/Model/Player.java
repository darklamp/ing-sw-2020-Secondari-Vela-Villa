package it.polimi.ingsw.Model;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.NickAlreadyTakenException;
import it.polimi.ingsw.Model.Exceptions.NoMoreMovesException;
import it.polimi.ingsw.Network.Messages.ServerMessage;
import it.polimi.ingsw.Network.SocketClientConnection;
import org.reflections.Reflections;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Set;

import static it.polimi.ingsw.Client.ClientState.BUILD;
import static it.polimi.ingsw.Client.ClientState.MOVE;

/**
 * Model representation for player.
 */
public class Player implements Serializable {

    /**
     * @see Serializable
     */
    private static final long serialVersionUID = 17756L;

    /**
     * List of the player's builders
     */
    private final ArrayList<Builder> builderList;

    /**
     * Pointer to the table on which the player is.
     */
    private GameTable gameTable;

    private final static Set<Class<?>> godClasses;

    static {
        Reflections reflections = new Reflections("it.polimi.ingsw.Model");
        godClasses = reflections.getTypesAnnotatedWith(God.class);
    }

    /**
     * This player's connection.
     */
    private transient SocketClientConnection connection;

    private final String nickname;

    /**
     * This player's god.
     */
    private String god;

    /**
     * Current state of the player,
     */
    private ClientState turnState;

    /**
     * True if the player still hasn't moved. Gets reset by {@link GameTable#nextTurn()}.
     * Used mostly by gods which can do the same thing twice.
     */
    private boolean firstTime = true;


    public String getGod() {
        return god;
    }


    public boolean isFirstTime() {
        return firstTime;
    }

    public void setFirstTime(boolean firstTime) {
        this.firstTime = firstTime;
    }


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
    }

    /**
     * @return first state of the player's god
     */
    private ClientState getFirstTurnState() {
        return builderList.get(0).getFirstState();
    }

    SocketClientConnection getConnection(){
        return this.connection;
    }

    /**
     * Remove builders from the GameTable
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
        else {
            this.nickname = nickname;
            this.god = god.toUpperCase();
            this.builderList = new ArrayList<>();
            this.gameTable = g;
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
     *
     * @param position position on which the builder is to be placed
     * @throws InvalidBuildException when the builder is trying to be placed on an occupied cell, or the player already has two builders, or the god is not in the server conf
     */
    public void initBuilderList(Cell position) throws InvalidBuildException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (position.getBuilder() != null || this.builderList.size() == 2 || this.god == null)
            throw new InvalidBuildException();
        if (!(GameTable.completeGodList.contains(this.god))) throw new InvalidBuildException();
        boolean flagFound = false;
        for (Class<?> cla : godClasses) {
            String name = cla.getAnnotation(God.class).name();
            if (name.equals(this.god)) {
                flagFound = true;
                Constructor<?> c;
                /* NB:
                     if this ( c = cla.get ...) throws, it means that the god was found between the implemented ones;
                     however, it was not implemented correctly, since it has no constructor.
                    */
                c = cla.getDeclaredConstructor(Cell.class, Player.class);
                /* NB:
                     if this throws, it means that the god was found between the implemented ones;
                     however, it was not implemented correctly, since the constructor is private.
                    */
                this.builderList.add((Builder) c.newInstance(position, this));
                this.turnState = gameTable.getPlayerIndex(this) == 1 ? getFirstTurnState() : ClientState.WAIT;
                break;
            }
        }
        if (!flagFound) {
            /* NB:
            if this executes, it means that the god couldn't be found between the
            implemented God classes, AND the god is in the GameTable.completeGodList, which
            is straight up wrong.
             */
            throw new InvalidBuildException();
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
     * @param builder builder to be checked (null if both)
     * @throws NoMoreMovesException if no feasible moves are found
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
            if (b1) {
                firstTime = true;
                for (Builder builder1 : builderList) {
                    builder1.clearPrevious();
                }
            }
        }
        if (builders == 0) throw new NoMoreMovesException(this);
        if (b1) {
            firstTime = true;
            for (Builder builder1 : builderList) {
                builder1.clearPrevious();
            }
        }

    }

    /**
     * Checks for available feasible builds
     *
     * @param builder builder to be checked (null if both)
     * @throws NoMoreMovesException if no feasible builds are found
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
            if (b1) {
                firstTime = true;
                for (Builder builder1 : builderList) {
                    builder1.clearPrevious();
                }
            }
        }
        if (builders == 0) throw new NoMoreMovesException(this);
        if (b1) {
            firstTime = true;
            for (Builder builder1 : builderList) {
                builder1.clearPrevious();
            }
        }
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

    synchronized void kick(int pIndex, boolean gameFinished) {
        this.connection.setGracefulClose();
        if (!gameFinished) gameTable.setNews(new News(ServerMessage.connClosed + "@@@" + pIndex, null), "PLAYERKICKED");
        this.connection.closeConnection();
    }
}
