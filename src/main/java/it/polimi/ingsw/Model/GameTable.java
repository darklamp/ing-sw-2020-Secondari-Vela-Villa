package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

public class GameTable {

    private static Cell[][] Table; /** 5x5 matrix representing game table **/
    private ArrayList<Player> players; /** arraylist filled with players **/
    private static ArrayList<Cell> arrayTable; /** simple object which contains the 25 pairs of coordinates from 0,0 to 4,4 as an arraylist of pair objects */
    private static GameTable instance; /** Singleton instance for GameTable **/
    private int currentPlayer = 0; /** current player index **/
    private final int playersNumber; /** number of players in game **/

    /**
     * returns current player
     **/
    public Player getCurrentPlayer() {
        return players.get(currentPlayer);
    }


    /**
     * skips to next turn
     */
    public void nextTurn(){
        if (currentPlayer == playersNumber - 1) currentPlayer = 0;
        else currentPlayer++;
    }


    /* initial observable pattern implementation via PropertyChangeSupport */

    private PropertyChangeSupport support; /** Listener helper object **/
    private Move news; /** Listener news **/

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    public void setNews(Move move, String type) {
        support.firePropertyChange(type, this.news, move);
        this.news = move;
    }

    /* end observable pattern */

    /**
     * Singleton public "constructor"
     * @param playersNumber number of players in game
     * @return single instance of GameTable
     */
    public static GameTable getInstance(int playersNumber){
        if (instance == null) {
            instance = new GameTable(playersNumber);
        }
        return instance;
    }


    /**
     * this method exists just to unit test w/ the singleton
     * TODO: delete in deploy
     */
    public static GameTable getDebugInstance(int playersNumber){
        return new GameTable(playersNumber);
    }


    public static GameTable getInstance(){
        if (instance == null) throw new NullPointerException();
        else return instance;
    }

    /**
     * private constructor for GameTable singleton
     */
    private GameTable(int playersNumber) {   //contructor method for GameTable

        Table = new Cell[5][5]; //create new Table
        arrayTable = new ArrayList<>();
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 5; j++){
                Table[i][j] = new Cell(i,j);
                arrayTable.add(Table[i][j]);
            }
        }
        players = null;
        this.playersNumber = playersNumber;
        support = new PropertyChangeSupport(this); //TODO: this o instance? credo sia la stessa cosa

    }

    /**
     * @param x X coord
     * @param y Y coord
     * @throws InvalidCoordinateException if coordinate is OOB
     */
    private static void isInvalidCoordinate(int x, int y) throws InvalidCoordinateException {
        if(x>4 || x<0 || y>4 || y<0) {
            throw new InvalidCoordinateException();
        }
    }
    protected static ArrayList<Cell> toArrayList(){
        return arrayTable;
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
        return players.stream().noneMatch(player -> sameNickname(player,nickname));
    }

    private boolean sameNickname(Player player,String nickname){
        return player.getNickname().equals(nickname);
    }
    protected void addPlayer(Player player){
        players.add(player);
    }
}
