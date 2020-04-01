package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameTable {

    private static Cell[][] Table; /** 5x5 matrix representing game table **/
    private ArrayList<Player> players; /** arraylist filled with players **/
    private static ArrayList<Cell> arrayTable; /** simple object which contains the 25 pairs of coordinates from 0,0 to 4,4 as an arraylist of pair objects */
    private static GameTable instance; /** Singleton instance for GameTable **/
    private int currentPlayer = 0; /** current player index **/
    private final int playersNumber; /** number of players in game **/
    public static final List<String> completeGodList = Arrays.asList("APOLLO","ARTEMIS","ATLAS","ATHENA","DEMETER","HEPHAESTUS","MINOTAUR","PAN","PROMETEUS");
    private static ArrayList<String> godChoices;
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

    ArrayList<String> getGodChoices(){
        return godChoices;
    }


    /* initial observable pattern implementation via PropertyChangeSupport */

    private PropertyChangeSupport support; /** Listener helper object **/
    private News news; /** Listener news **/

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    public void setNews(News news, String type) {
        support.firePropertyChange(type, this.news, news);
        this.news = news;
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
    /**
     * this method exists just to unit test w/ the singleton
     * TODO: delete in deploy
     */
    public void setDebugInstance(){
        instance = this;
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

    /**
     * @param nickname contains nickname to be checked
     * @return true if the nickname isn't already in use, false otherwise
     */
    protected boolean isValidPlayer(String nickname){
        if(players == null) {
            players = new ArrayList<Player>();
            return true;
        }
        return players.stream().noneMatch(player -> sameNickname(player,nickname));
    }

    /**
     * @return true if player's nickname is the same as nickname
     */
    private boolean sameNickname(Player player,String nickname){
        return player.getNickname().equals(nickname);
    }

    /**
     * @param player player to be added
     */
    protected void addPlayer(Player player){

        players.add(player);
        setNews(new News(player), "ADDPLAYER"); /* sets news for view */

    }
}
