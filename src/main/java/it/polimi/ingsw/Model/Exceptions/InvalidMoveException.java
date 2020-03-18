package it.polimi.ingsw.Model.Exceptions;

public class InvalidMoveException extends Exception{
    public InvalidMoveException() {
        super();
    }
    public InvalidMoveException(String arg){
        super(arg);
    }
}
