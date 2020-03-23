package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;

import java.util.ArrayList;

public class GameTable {

    private static Cell[][] Table;
    private ArrayList<Player> players;

    public GameTable() {   //contructor method for GameTable

        Table = new Cell[5][5]; //create new Table
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 5; j++){
                Table[i][j] = new Cell(i,j);
            }
        }
        players = null;

    }
    private static void isInvalidCoordinate(int x, int y) throws InvalidCoordinateException {
        if(x>4 || x<0 || y>4 || y<0) {
            throw new InvalidCoordinateException();
        }
    }
    protected static Cell getCell(int x,int y) throws InvalidCoordinateException{
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
            }
        return true;
    }
    protected void addPlayer(Player player){
        players.add(player);
    }
}
