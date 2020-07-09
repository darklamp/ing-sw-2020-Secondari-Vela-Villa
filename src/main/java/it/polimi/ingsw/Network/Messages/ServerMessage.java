/*
 * Santorini
 * Copyright (C)  2020  Alessandro Villa and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Affero General Public License as
 *      published by the Free Software Foundation, either version 3 of the
 *      License, or (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Affero General Public License for more details.
 *
 *      You should have received a copy of the GNU Affero General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * E-mail contact addresses:
 * darklampz@gmail.com
 * alessandro17.villa@mail.polimi.it
 *
 */

package it.polimi.ingsw.Network.Messages;

import java.io.Serializable;

/**
 * Messages to be sent from server to client(s)
 */
public final class ServerMessage implements Serializable, Message {

    private static final long serialVersionUID = 17756L;

    public static final String welcome = "Welcome!";
    public static final String reloadGameChoice = "Please enter your name if you want to start a new game,\n or enter \"R\" if you want to rejoin a saved game: ";
    public static final String userAlreadyTaken = "Username already taken. Please enter a different one: ";
    public static final String firstPlayer = "Looks like you're the first player to connect.\n" +
            "You get to decide the number of players.\n" +
            "Please input a natural lower than 4 and higher than 1: ";
    public final static ErrorMessage wrongNumber = new ErrorMessage("Wrong number. Please try again.");
    public final static ErrorMessage notYourTurn = new ErrorMessage("It's probably not your turn.");
    public final static ErrorMessage invalidNews = new ErrorMessage("Invalid move. Please try again.");
    public final static ErrorMessage genericErrorMessage = new ErrorMessage("Bad request.");
    public final static ErrorMessage abortMessage = new ErrorMessage("Game interrupted abruptly");
    public static String nextChoice = "Please choose: ";
    public static String firstBuilderPos = "Insert the starting position of your first worker (row,col):";
    public static String secondBuilderPos = "Insert the starting position of your second worker (row,col):";
    public static String cellNotAvailable = "This cell is not available, try another one";
    public static String connClosed = "Connection closed.";
    public final static ErrorMessage serverDown = new ErrorMessage("Server interrupted unexpectedly.");
    public static String welcomeNoPersistence = "Please enter your name.";
    public static String lastGod = "You're left with @@@";
    public final static ErrorMessage gameLost = new ErrorMessage("Looks like you lost!");
    public final static ErrorMessage invalidNumber = new ErrorMessage("Invalid number");


}
