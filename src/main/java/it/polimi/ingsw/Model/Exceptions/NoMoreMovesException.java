package it.polimi.ingsw.Model.Exceptions;

import it.polimi.ingsw.Model.Player;

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
