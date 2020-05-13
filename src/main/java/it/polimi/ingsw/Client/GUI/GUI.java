package it.polimi.ingsw.Client.GUI;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Client.Ui;
import it.polimi.ingsw.Network.ServerMessage;
import it.polimi.ingsw.View.CellView;
import javafx.application.Application;
import javafx.application.Platform;

import java.util.Scanner;

public class GUI implements Ui, Runnable {
    private static boolean isReady = false;
    private static LoginWindowController loginController;
    private static volatile boolean newValue = false;
    private static boolean guiInitialized = false;
    private static int playerIndex, playersNumber;

    static int getPlayerIndex() {
        return playerIndex;
    }

    static int getPlayersNumber() {
        return playersNumber;
    }


    void setOut(String s) {
        out = s;
        newValue = true;
    }

    private static String out;


    @Override
    public void run() {
        Application.launch(GUIClient.class);
    }

    protected synchronized void setReady(boolean b) {
        isReady = b;
        loginController = (LoginWindowController) GUIClient.getController();
    }
    public synchronized boolean isReady(){
        return isReady;
    }

    @Override
    public void process(String input) {
        if (input.contains("[INIT]")) { /* if the string contains this prefix, it's an initialization string and it must be treated as such */
            String[] inputs = input.split("@@@");
            playerIndex = Integer.parseInt(inputs[1]);
            playersNumber = Integer.parseInt(inputs[2]);
        } else if (input.contains("[CHOICE]")) {
            if (input.contains("POS")) {
                if (!guiInitialized) {
                    Platform.runLater(() -> GUIClient.getController().switchScene("/Main.fxml"));
                    guiInitialized = true;
                }
                Platform.runLater(() -> GUIClient.getController().getStartingPositions());
            } else Platform.runLater(() -> loginController.parseGodChoice(input));
        } else if (input.contains(ServerMessage.firstPlayer)) Platform.runLater(() -> loginController.firstPlayer());
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
        switch (newState) {
            case MOVE -> Platform.runLater(() -> GUIClient.getController().setText("asd"));
        }
    }

    @Override
    public String nextLine(Scanner in) {
        while (!newValue) {
            Thread.onSpinWait();
        }
        newValue = false;
        return out;
    }
}