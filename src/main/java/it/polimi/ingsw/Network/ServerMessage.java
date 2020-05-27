package it.polimi.ingsw.Network;

import java.io.Serializable;

/**
 * Messages to be sent from server to client(s)
 */
public final class ServerMessage implements Serializable {

    private static final long serialVersionUID = 17756L;

    public static final String welcome = "Welcome!";
    public static final String reloadGameChoice = "Please enter your name if you want to start a new game,\n or enter \"R\" if you want to rejoin a saved game: ";
    public static final String userAlreadyTaken = "Username already taken. Please enter a different one: ";
    public static final String firstPlayer = "Looks like you're the first player to connect. You get to decide the number of players.\n" +
            "Please input a natural lower than 4 and higher than 1: ";
    public static String wrongNumber = "[ERROR]@@@Wrong number. Please try again.";
    public static String notYourTurn = "[ERROR]@@@It's probably not your turn.";
    public static String invalidNews = "[ERROR]@@@Invalid move. Please try again.";
    public static String genericErrorMessage = "[ERROR]@@@Bad request.";
    public static String abortMessage = "[ERROR]@@@Game interrupted abruptly";
    public static String nextChoice = "Please choose: ";
    public static String firstBuilderPos = "Insert the starting position of your first worker (row,col):";
    public static String secondBuilderPos = "Insert the starting position of your second worker (row,col):";
    public static String cellNotAvailable = "This cell is not available, try another one";
    public static String connClosed = "Connection closed.";
    public static String serverDown = "Server interrupted unexpectedly.";
    public static String welcomeNoPersistence = "Please enter your name.";
    public static String lastGod = "You're left with @@@";
}
