package it.polimi.ingsw.Controller.Exceptions;

/**
 * Thrown when the player's current state doesn't match the move the client intends to make
 * eg.: client tries to MOVE; server reads client's actual state, which is  --> thrown
 */
public class IllegalTurnStateException extends Exception {

    public IllegalTurnStateException() { super();
    }
}
