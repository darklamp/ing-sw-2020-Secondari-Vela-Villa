package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;
import it.polimi.ingsw.Model.Exceptions.InvalidPlayersNumberException;
import it.polimi.ingsw.View.CellView;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameTable {

    private final Cell[][] Table; /** 5x5 matrix representing game table **/
    private ArrayList<Player> players; /** arraylist filled with players **/
    private static ArrayList<Cell> arrayTable; /** simple object which contains the 25 pairs of coordinates from 0,0 to 4,4 as an arraylist of pair objects */
    private static GameTable instance; /** Singleton instance for GameTable **/ //TODO remove!!
    private int currentPlayer = 1; /** current player index **/
    private final int playersNumber; /** number of players in game **/
    public static final List<String> completeGodList = Arrays.asList("APOLLO","ARTEMIS","ATHENA","ATLAS","DEMETER","HEPHAESTUS","MINOTAUR","PAN","PROMETEUS"); /* list containing all the basic gods */
    private ArrayList<String> godChoices; /* list of gods chosen by the first player to be available in the game */
    private Builder currentBuilder = null; /* variable which holds the current builder being used by the player */
    private static boolean athenaMove = false;

    public Player getCurrentPlayer() {
        return players.get(currentPlayer);
    }


    /**
     * The purpose of this function is going to the next turn;
     * specifically, what this function does is:
     * - reset the boolean firstTime in currentPlayer to true
     * - reset the currentBuilder attribute to a default null value
     * - increase the currentPlayer index, so as to effectively skip to next player
     */
    public void nextTurn(){
        getCurrentPlayer().setFirstTime(true);
        getCurrentBuilder().resetState();
        setCurrentBuilder(null);
        if (currentPlayer == playersNumber - 1) currentPlayer = 0;
        else currentPlayer++;
    }


    public static List<String> getCompleteGodList() {
        return completeGodList;
    }

    int getPlayerIndex(Player player){
        if (players.get(0).equals(player)) return 0;
        else if (players.get(1).equals(player)) return 1;
        else return playersNumber == 3 ? 2 : -1; // -1 is error case
    }

    public Builder getCurrentBuilder() {
        return currentBuilder;
    }

    public void setCurrentBuilder(Builder currentBuilder) {
        this.currentBuilder = currentBuilder;
    }

    ArrayList<String> getGodChoices(){
        return godChoices;
    }


    /* initial observable pattern implementation via PropertyChangeSupport */

    //TODO: this o instance? credo sia la stessa cosa
    //TODO: this o instance? credo sia la stessa cosa
    private PropertyChangeSupport support = new PropertyChangeSupport(this); /** Listener helper object **/
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

    public CellView[][] getBoardCopy(){
        CellView[][] out = new CellView[5][5];
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 5; j++){
                out[i][j] = Table[i][j].getModelView(this);
            }
        }
        return out;
    }


    public static GameTable getInstance(int playersNumber){
        return new GameTable(playersNumber);
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



   /* public static GameTable getInstance(){
        if (instance == null) throw new NullPointerException();
        else return instance;
    }*/

    public GameTable(int playersNumber) {   //contructor method for GameTable

        Table = new Cell[5][5]; //create new Table
        arrayTable = new ArrayList<>();
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 5; j++){
                Table[i][j] = new Cell(i,j,this);
                arrayTable.add(Table[i][j]);
            }
        }
        players = null;
        this.playersNumber = playersNumber;

    }

    @Deprecated
    public GameTable(int playersNumber, ArrayList<Integer> godChoices) {   //contructor method for GameTable

        Table = new Cell[5][5]; //create new Table
        arrayTable = new ArrayList<>();
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 5; j++){
                Table[i][j] = new Cell(i,j,this);
                arrayTable.add(Table[i][j]);
            }
        }
        players = null;
        this.playersNumber = playersNumber;
        ArrayList<String> gods = new ArrayList<>();
        for (Integer godChoice : godChoices) {
            gods.add(completeGodList.get(godChoice));
        }
        this.godChoices = gods;

    }

    public void setGods(ArrayList<Integer> godChoices){
        ArrayList<String> gods = new ArrayList<>();
        for (Integer godChoice : godChoices) {
            gods.add(completeGodList.get(godChoice));
        }
        this.godChoices = gods;
    }

    public void setPlayers(ArrayList<Player> players){
        this.players = players;
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
    public Cell getCell(int x,int y) throws InvalidCoordinateException{
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

    }

    static void setAthenaMove(boolean newValue){
        athenaMove = newValue;
    }
    static boolean getAthenaMove(){ return athenaMove; }
}
