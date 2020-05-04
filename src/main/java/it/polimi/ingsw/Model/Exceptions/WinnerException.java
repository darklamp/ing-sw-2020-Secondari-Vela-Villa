package it.polimi.ingsw.Model.Exceptions;

import it.polimi.ingsw.Model.Player;

/**
 * Thrown when the server determines a player as the winner.
 */
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
