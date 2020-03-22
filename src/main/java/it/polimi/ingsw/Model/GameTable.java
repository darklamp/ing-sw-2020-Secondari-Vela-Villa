package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;

public class GameTable {

    private Cell[][] Table;

    public GameTable() {   //contructor method for GameTable

        Table= new Cell[5][5]; //create new Table

    }
    private void InvalidCoordinate(int x, int y) throws InvalidCoordinateException {
        if(x>4 || x<0 || y>4 || y<0) {
            throw new InvalidCoordinateException();
        }
    }
    public Cell getCell(int x,int y) {
        try {
            InvalidCoordinate(x,y);
            return Table[x][y]; //ritorna la cella con righa x e colonna y
        }
        catch(InvalidCoordinateException e) {
            //se vengono inserite x o y negativi o maggiori di 5
        }
    }
    public boolean CheckGameover(){
        //return true if there is just one player in game
    }
    public int getPlayerIn(){
        //ritorna il numero di player ancora in gioco
    }
}
