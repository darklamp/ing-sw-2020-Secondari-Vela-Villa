package it.polimi.ingsw.Model;

public class GameTable {

    private Cell[][] Table;

    public GameTable() {   //contructor method for GameTable

        Table= new Cell[5][5]; //create new Table
    }

    public Cell getCell(int x,int y) {
        try {
            return Table[x][y]; //ritorna la cella con righa x e colonna y
        }
        chatch(negativeoroverthanfive e) {//se vengono inserite x o y negativi o maggiori di 5
        }
    }
    public boolean CheckGameover(){
        //return true if there is just one player in game
    }
    public int getPlayerIn(){
        //ritorna il numero di player ancora in gioco
    }
}
