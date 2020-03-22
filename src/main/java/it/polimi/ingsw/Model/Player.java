package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.NickAlreadyTakenException;

public class Player {

    private Builder[] BuilderList; //array of builders

    private String Nickname; //private attribute for the Player's ID

    private boolean IsInGame; //private boolean to know if the player is still in the Game

    private int InGameTurn; //private number to know the turn of the player


    public void setBuilderList(){ //setter for the array of builders
        BuilderList = new Builder[2];
    }

    public Player(String Nickname) {   //contructor method for player
        try {
            this.Nickname = Nickname;  //If the nickname is accepted,the player'll be insert in the game
            this.IsInGame = true;
        }

        catch (NullPointerException e) {

        }
        catch(NickAlreadyTakenException e) { //two players can't choose the same nickname in the same game
        }
    }

    public void UpdatePlayerStatus(){
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

    public void

    public void setInGameTurn(int inGameTurn) {
        this.InGameTurn = inGameTurn;
    }
}
