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

import it.polimi.ingsw.Client.ClientState;

import java.io.Serializable;

/**
 * Message sent to all clients every time a state change happens.
 */
public class GameStateMessage implements Serializable, Message {

    private static final long serialVersionUID = 17756L;

    private final ClientState p1, p2, p3;

    private final String player1, player2, player3;

    private final int currentPlayer;

    public GameStateMessage(ClientState p1, ClientState p2, ClientState p3, String player1, String player2, String player3, int currentPlayer) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.player1 = player1;
        this.player2 = player2;
        this.player3 = player3;
        this.currentPlayer = currentPlayer;
    }

    /**
     * Getter for player's ClientState by index.
     *
     * @param i index of the player.
     * @return player's CLientState
     */
    public ClientState get(int i) {
        return switch (i) {
            case 1 -> p2;
            case 2 -> p3;
            default -> p1;
        };
    }

    /**
     * Getter for player nickname by index.
     *
     * @param i index of the player.
     * @return player's nickname
     */
    public String getName(int i) {
        return switch (i) {
            case 1 -> player2;
            case 2 -> player3;
            default -> player1;
        };
    }

    public String getCurrentPlayer() {
        return switch (currentPlayer) {
            case 1 -> player2;
            case 2 -> player3;
            default -> player1;
        };
    }
}
