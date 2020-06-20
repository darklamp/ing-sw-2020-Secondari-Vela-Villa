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
