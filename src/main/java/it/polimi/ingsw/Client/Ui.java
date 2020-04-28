package it.polimi.ingsw.Client;

import java.io.IOException;

public interface Ui {
    void process(String input);
    void waitForIP(Client client) throws IOException;
}
