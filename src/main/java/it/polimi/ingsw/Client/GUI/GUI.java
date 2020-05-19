package it.polimi.ingsw.Client.GUI;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Client.Ui;
import it.polimi.ingsw.Network.ServerMessage;
import it.polimi.ingsw.View.CellView;
import javafx.application.Application;
import javafx.application.Platform;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Scanner;

public class GUI implements Ui, Runnable, PropertyChangeListener {
    private static boolean isReady = false;
    private static LoginWindowController loginController;
    private static boolean guiInitialized = false;


    synchronized static short getPlayerIndex() {
        return (short) Client.getPlayerIndex();
    }

    static short getPlayersNumber() {
        return (short) Client.getPlayersNumber();
    }


    @Override
    public void run() {
        Application.launch(GUIClient.class);
    }

    protected synchronized void setReady(boolean b) {
        isReady = b;
        GUIClient.addPropertyChangeListener(this);
        loginController = (LoginWindowController) GUIClient.getController();
    }
    public synchronized boolean isReady(){
        return isReady;
    }

    @Override
    synchronized public void process(String input) {
        if (input.contains("[ERROR]")) {
            String[] inputs = input.split("@@@");
            Platform.runLater(() -> GUIClient.getController().setError(inputs[1]));
        } else if (input.contains("[INIT]")) { /* if the string contains this prefix, it's an initialization string and it must be treated as such */
            String[] inputs = input.split("@@@");
            Client.setPlayerIndex((short) Integer.parseInt(inputs[1]));
            Client.setPlayersNumber((short) Integer.parseInt(inputs[2]));
        } else if (input.contains("[CHOICE]")) {
            String[] inputs = input.split("@@@");
            if (inputs[1].equals("GODS")) {
                Client.setPlayersNumber((short) Integer.parseInt(inputs[2]));
                Platform.runLater(() -> loginController.firstPlayerGods());
            } else if (input.contains("POS")) {
                if (!guiInitialized) {
                    Platform.runLater(() -> GUIClient.getController().switchScene("/Main.fxml"));
                    guiInitialized = true;
                }
                Platform.runLater(() -> GUIClient.getController().getStartingPositions(false));
            } else Platform.runLater(() -> loginController.parseGodChoice(input));
        } else if (input.contains(ServerMessage.firstPlayer)) Platform.runLater(() -> loginController.firstPlayer());
        else if (input.equals(ServerMessage.cellNotAvailable))
            Platform.runLater(() -> GUIClient.getController().getStartingPositions(true));
        else Platform.runLater(() -> loginController.setText(input));
    }

    @Override
    public void waitForIP(Client client) {
        Platform.runLater(() -> LoginWindowController.waitForIP(client));
    }

    @Override
    public void showTable(CellView[][] table) {
        Platform.runLater(() -> GUIClient.getController().updateTable(table));
    }

    @Override
    public void processTurnChange(ClientState newState) {
        Platform.runLater(() -> GUIClient.getController().setMove(newState));
    }

    @Override
    public String nextLine(Scanner in) {
        while (!newValue) {
            Thread.onSpinWait();
        }
        newValue = false;
        return out;
    }

    private volatile boolean newValue = false;
    private static String out;

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        out = (String) evt.getNewValue();
        newValue = true;
    }
}