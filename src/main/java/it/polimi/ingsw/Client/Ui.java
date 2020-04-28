package it.polimi.ingsw.Client;

import it.polimi.ingsw.View.CellView;

import java.io.IOException;

public interface Ui {
    void process(String input);
    void waitForIP(Client client) throws IOException;
    void showTable(CellView[][] table);
}
