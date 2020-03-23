package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;

import java.util.ArrayList;

public class GameTable {

    private Cell[][] Table;
    private ArrayList<Player> players;

    public GameTable() {   //contructor method for GameTable

        Table = new Cell[5][5]; //create new Table
        players = null;

    }
    private void isInvalidCoordinate(int x, int y) throws InvalidCoordinateException {
        if(x>4 || x<0 || y>4 || y<0) {
            throw new InvalidCoordinateException();
        }
    }
    protected Cell getCell(int x,int y) throws InvalidCoordinateException{
            isInvalidCoordinate(x,y);
            return Table[x][y]; //ritorna la cella con righa x e colonna y
    }/*
    public boolean CheckGameover(){
        //return true if there is just one player in game
    }
    public int getPlayerIn(){
        //ritorna il numero di player ancora in gioco
    }*/

    protected boolean isValidPlayer(String nickname){
        if(players == null) {
            players = new ArrayList<Player>();
            return true;
        }
        for (Player p :
                players) {
            if ((p.getNickname()).equals(nickname)) {
                return false;
            }
            else return true;
            }
        return true;
    }
    protected void addPlayer(Player player){
        players.add(player);
    }
}
