package it.polimi.ingsw.Network;

public interface ClientConnection{

    void closeConnection();

    void addObserver(Observer<String> observer);

    void asyncSend(Object message);
}
