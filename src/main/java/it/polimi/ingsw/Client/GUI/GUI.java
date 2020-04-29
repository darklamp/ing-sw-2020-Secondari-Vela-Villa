package it.polimi.ingsw.Client.GUI;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Client.Ui;
import it.polimi.ingsw.View.CellView;
import javafx.application.Application;
import javafx.application.Platform;

public class GUI implements Ui,Runnable {
    private static boolean isReady = false;
    private static LoginWindowController controller;
    
    @Override
    public void run() {
        Application.launch(GUIClient.class);
    }
    protected synchronized void setReady(boolean b){
        isReady = b;
        controller = GUIClient.getController();
    }
    public synchronized boolean isReady(){
        return isReady;
    }

    @Override
    public void process(String input) {
        Platform.runLater(() -> controller.setText(input));
    }

    @Override
    public void waitForIP(Client client) {
        Platform.runLater(() -> LoginWindowController.waitForIP(client));
    }

    @Override
    public void showTable(CellView[][] table) {

    }
}