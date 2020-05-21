package it.polimi.ingsw.Model;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.NickAlreadyTakenException;
import it.polimi.ingsw.Network.ServerMessage;
import it.polimi.ingsw.Network.SocketClientConnection;

import java.util.ArrayList;

import static it.polimi.ingsw.Client.ClientState.MOVE;
import static it.polimi.ingsw.Client.ClientState.MOVEORBUILD;

public class Player {

    private final ArrayList<Builder> builderList; //array of builders
    
    private GameTable gameTable;
    private SocketClientConnection connection;

    private final String nickname; //private attribute for the Player's ID

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

    public void setGod(Integer god){
        this.god = GameTable.getCompleteGodList().get(god);
        this.turnState = gameTable.getPlayerIndex(this) == 1 ? getFirstTurnState(god) : ClientState.WAIT;
    }

    private ClientState getFirstTurnState(Integer god){
        return (god == 8) ? MOVEORBUILD : MOVE; /* if god is prometheus, he can move as well as build at first */
    }

    SocketClientConnection getConnection(){
        return this.connection;
    }

    void removeBuilders(){
        this.getBuilderList().get(0).getPosition().setBuilder(null);
        this.getBuilderList().get(1).getPosition().setBuilder(null);
    }

    /**
     * DEBUG package-private constructor only used in tests
     **/
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

    public boolean hasNoBuilder(){
        return this.builderList.size() == 0;
    }

    ArrayList<Builder> getBuilderList() {
        return this.builderList;
    }

    public String getNickname() {
        return nickname;
    }

    synchronized void kick(int pIndex) {
        gameTable.setNews(new News(ServerMessage.connClosed + "@@@" + pIndex, null), "PLAYERKICKED");
        this.connection.closeConnection();
    }
}
