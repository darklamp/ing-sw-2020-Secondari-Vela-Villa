package it.polimi.ingsw.Model.Exceptions;

import it.polimi.ingsw.Model.Player;

public class WinnerException extends Exception{
    private Player winner = null;
    private WinnerException(){
        super();
    }
    public WinnerException(Player player){
        this();
        this.winner = player;
    }
    public Player getPlayer() {
        return winner;
    }
}
