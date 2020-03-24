package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.NickAlreadyTakenException;

public class Player {

    private Builder[] builderList; //array of builders

    private String nickname; //private attribute for the Player's ID

    private boolean IsInGame; //private boolean to know if the player is still in the Game

    private int InGameTurn; //private number to know the turn of the player


    public void setBuilderList(){ //setter for the array of builders
        builderList = new Builder[2];
    }

    public Player(String nickname, GameTable gametable) throws NickAlreadyTakenException, NullPointerException {   //contructor method for player
        // TODO: probabilmente al builder andrà passato il tipo di dio
            if (gametable == null) throw new NullPointerException();
            else if (!gametable.isValidPlayer(nickname)) throw new NickAlreadyTakenException();
            else {
                this.nickname = nickname;  //If the nickname is accepted,the player'll be insert in the game
                this.IsInGame = true;
                gametable.addPlayer(this); //TODO: forse va nel controller?
                //TODO : inizializzare builderList (controller)
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
