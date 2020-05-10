package it.polimi.ingsw.Client;

import it.polimi.ingsw.View.CellView;

import java.io.IOException;
import java.util.Scanner;

public interface Ui {

    /**
     * Processes input string
     */
    void process(String input);

    /**
     * Wait for IP address input
     */
    void waitForIP(Client client) throws IOException;

    /**
     * Responsible for printing game table
     * @param table to be printed
     */
    void showTable(CellView[][] table);

    /**
     * Reacts to new state by printing something to screen
     *
     * @param newState state to be processed
     */
    void processTurnChange(ClientState newState);

    String nextLine(Scanner in);
}
