package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidGodException;
import it.polimi.ingsw.Model.Exceptions.NickAlreadyTakenException;

import java.util.ArrayList;

public class Player {

    private ArrayList<Builder> builderList; //array of builders

    private final String nickname; //private attribute for the Player's ID

    private boolean isInGame; //private boolean to know if the player is still in the Game

    private final String god;

    private TurnState turnState;

    public boolean isFirstTime() {
        return firstTime;
    }

    public void setFirstTime(boolean firstTime) {
        this.firstTime = firstTime;
    }

    private boolean firstTime = true;

    /**
     * Constructor for player; once the object is built, it gets added to the player list in GameTable.
     * @param nickname player's nickname
     * @param god player's chosen god
     * @throws NickAlreadyTakenException when nick already taken
     * @throws NullPointerException when the gametable has not been initialized (shouldn't happen btw)
     * @throws InvalidGodException if the chosen god is invalid
     */
    public Player(String nickname, String god) throws NickAlreadyTakenException, NullPointerException, InvalidGodException {   //contructor method for player
            if (GameTable.getInstance() == null) throw new NullPointerException();
            else if (!GameTable.getInstance().isValidPlayer(nickname)) throw new NickAlreadyTakenException();
            else if (!isValidGod(god)) throw new InvalidGodException();
            else {
                this.nickname = nickname;  //If the nickname is accepted,the player'll be insert in the game
                this.isInGame = true;
                this.god = god.toUpperCase();
                this.builderList = new ArrayList<>();
                GameTable.getInstance().addPlayer(this);
                //TODO : inizializzare builderList (controller)
            }
    }

    /** DEBUG package-private constructor TODO remove in deploy **/
    Player(String nickname, GameTable g, String god) throws NickAlreadyTakenException, NullPointerException, InvalidGodException {   //contructor method for player
        if (GameTable.getInstance() == null) throw new NullPointerException();
        else if (!GameTable.getInstance().isValidPlayer(nickname)) throw new NickAlreadyTakenException();
       // else if (!isValidGod(god)) throw new InvalidGodException();
        else {
            this.nickname = nickname;  //If the nickname is accepted,the player'll be insert in the game
            this.isInGame = true;
            this.god = god.toUpperCase();
            this.builderList = new ArrayList<>();
            GameTable.getInstance().addPlayer(this);
            //TODO : inizializzare builderList (controller)
        }
    }

    String getGod(){
        return this.god;
    }

    /**
     * Checks if the provided god is valid and selectable; if it is, it returns true and
     * it removes the god from the selectable pool, so other player(s) can't pick it
     * @return true if valid and selectable, false otherwise
     */
    private boolean isValidGod(String god){
        ArrayList<String> godChoices = GameTable.getInstance().getGodChoices();
        if (godChoices.contains(god.toUpperCase())) {
            godChoices.remove(god);
            return true;
        }
        else return false;
    }

    public TurnState getState(){
        return this.turnState;
    }
    public void setState(TurnState state){
        this.turnState = state;
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
            case "PROMETEUS" -> this.builderList.add(new Prometeus(position, this));
            default -> throw new InvalidBuildException();
        }
    }
  /*  public void UpdatePlayerStatus(){
        this.IsInGame = false;      //why set false?Beacuse the bit is setted at the start of the game to true,so I just have to change it to false.
    }

    public boolean CanMove(){
        //return true if the player can move,otherwise set false
    }

    public void MoveTo(Cell cell) { //the cell where player wants to go
    }
    public Cell MoveFrom(){
        //return the cell before the movement
    }

    public void Build(Cell cell){
        //the cell where the player wants to build
    }

    public int getInGameTurn() {
        return InGameTurn;
    }

    public void setInGameTurn(int inGameTurn) {
        this.InGameTurn = inGameTurn;
    }*/

    public String getNickname() {
        return nickname;
    }
}
