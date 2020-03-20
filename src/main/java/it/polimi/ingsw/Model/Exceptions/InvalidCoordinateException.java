package it.polimi.ingsw.Model.Exceptions;

public class InvalidCoordinateException extends Exception{
    public InvalidCoordinateException () {
        super();
    }
    public InvalidCoordinateException (String arg){
        super(arg);
    }
}