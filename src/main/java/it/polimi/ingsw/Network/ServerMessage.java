package it.polimi.ingsw.Network;

import java.io.Serializable;

/**
 * Messages to be sent from server to client(s)
 */
public final class ServerMessage implements Serializable {

    public static final String welcome = "Welcome!\nWhat's your name?";
    public static final String userAlreadyTaken = "Username already taken. Please enter a different one: ";
    public static final String firstPlayer = "Looks like you're the first player to connect. You get to decide the number of players.\n" +
            "Please input a natural lower than 4 and higher than 1: ";
    public static String wrongNumber = "[ERROR]@@@Wrong number. Please try again.";
    public static String makeYourMove = "It's your turn. Please make a move: ";
    public static String notYourTurn = "[ERROR]@@@Wait for your turn you heathen";
    public static String invalidNews = "[ERROR]@@@Invalid move. Please try again.";
    public static String genericErrorMessage = "[ERROR]@@@Richiesta no buena.";
    public static String abortMessage = "[ERROR]@@@Game interrupted abruptly";
    public static String nextChoice = "Please choose: ";
    public static String firstBuilderPos = "Insert the starting position of your first worker (row,col):";
    public static String secondBuilderPos = "Insert the starting position of your second worker (row,col):";
    public static String cellNotAvailable = "This cell is not available, try another one";
    public static String connClosed = "Connection closed.";

}
