package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

public class GameTable {

    private static Cell[][] Table;
    private ArrayList<Player> players;
    private static GameTable instance; /* Singleton instance for GameTable */
    private int currentPlayer = 0;
    private final int playersNumber;

    public Player getCurrentPlayer() {
        return players.get(currentPlayer);
    }



    public void nextTurn(){
        if (currentPlayer == playersNumber - 1) currentPlayer = 0;
        else currentPlayer++;
    }


    /* initial observable pattern implementation via PropertyChangeSupport */

    private PropertyChangeSupport support; // helper object
    private Move news;

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }


    public void setNews(Cell cell, Builder builder, String type) {
        Move move = new Move(cell,builder);
        support.firePropertyChange(type, this.news, move);
        this.news = move;
    }

    public void setNews(Move move, String type) {
        support.firePropertyChange(type, this.news, move);
        this.news = move;
    }

    /* end observable pattern */

    public static GameTable getInstance(int playersNumber){
        if (instance != null) throw new NullPointerException();
        else {
            instance = new GameTable(playersNumber);
            return instance;
        }
    }


    public static GameTable getInstance(){
        if (instance == null) throw new NullPointerException();
        else return instance;
    }

    private GameTable(int playersNumber) {   //contructor method for GameTable

        Table = new Cell[5][5]; //create new Table
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 5; j++){
                Table[i][j] = new Cell(i,j);
            }
        }
        players = null;
        this.playersNumber = playersNumber;
        support = new PropertyChangeSupport(this); //TODO: this o instance? credo sia la stessa cosa

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
    public boolean checkGameover(){
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
