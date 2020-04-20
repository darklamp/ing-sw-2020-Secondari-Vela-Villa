package it.polimi.ingsw.Network;

import java.beans.PropertyChangeListener;

public interface ClientConnection{

    void closeConnection();

    //void addObserver(Observer<String> observer);
    void addPropertyChangeListener(PropertyChangeListener pcl);

    void asyncSend(Object message);
}
