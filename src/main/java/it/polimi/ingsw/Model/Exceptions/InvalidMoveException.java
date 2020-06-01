package it.polimi.ingsw.Model.Exceptions;

/**
 * Thrown when a Move move is checked as invalid.
 */
public class InvalidMoveException extends Exception{
    public InvalidMoveException() {
        super();
    }

    public InvalidMoveException(String arg){
        super(arg);
    }
}
