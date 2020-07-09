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

package it.polimi.ingsw.Client;

import it.polimi.ingsw.Network.Messages.Message;
import it.polimi.ingsw.View.CellView;

import java.util.Scanner;

public interface Ui {

    /**
     * Processes input String message.
     * Message can be either a simple String or a "message" String.
     * Message strings are formatted as follows:
     * [ TYPE ] + n * ( SEPARATOR + ARG )
     * with n = #arguments
     * SEPARATOR = @@@
     * TYPE =
     * - CHOICE : server prompts client for choice; used when asking for gods, positions
     *
     * @param input message to be processed
     */
    void process(String input);

    /**
     * Processes input {@link Message}.
     * @param input message to be processed
     */
    void process(Message input);

    /**
     * Starts Client thread, asking for IP input if necessary.
     * @param client Client to be ran
     */
    void waitForIP(Client client);

    /**
     * Responsible for printing game table.
     * @param table to be printed
     */
    void showTable(CellView[][] table);

    /**
     * Responsible for reacting to new {@link ClientState}
     * @param newState state to be processed
     */
    void processTurnChange(ClientState newState);

    /**
     * This method unifies the nextLine function for every Ui, so that
     * the Client can call it without having to do boring instanceofs
     *
     * @param in input Scanner
     * @return string to be parsed and eventually written to output
     */
    @SuppressWarnings("SpellCheckingInspection")
    String nextLine(Scanner in);
}
