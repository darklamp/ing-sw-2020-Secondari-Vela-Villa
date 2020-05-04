package it.polimi.ingsw.Model.Exceptions;

import it.polimi.ingsw.Model.Player;

/**
 * Thrown when a player has no feasible moves left.
 */
public class NoMoreMovesException extends  Exception{
    private Player player;
    private NoMoreMovesException(){
        super();
    }
    public NoMoreMovesException(Player p){
        this();
        this.player = p;
    }

    public Player getPlayer() {
        return player;
    }
}
