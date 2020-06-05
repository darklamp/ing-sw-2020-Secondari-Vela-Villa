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
    /**
     * gui's ready state
     */
    private static boolean isReady = false;
    /**
     * Commodity reference to login controller.
     */
    private static LoginWindowController loginController;
    /**
     * gui's fully initialized state
     */
    private static boolean guiInitialized = false;
    /**
     * variable on which {@link GUI#showTable} and {@link GUI#processTurnChange} synchronize
     */
    private static boolean turnChanged = false;
    /**
     * variable on which {@link GUI#nextLine} is synchronized
     */
    private volatile boolean newValue = false;
    /**
     * String to be printed to output socket, via {@link GUI#nextLine}
     */
    private static String out;

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

    /**
     * Sets GUI's ready state
     */
    protected synchronized void setReady(boolean b) {
        isReady = b;
        GUIClient.addPropertyChangeListener(this);
        loginController = (LoginWindowController) GUIClient.getController();
    }

    public synchronized boolean isReady() {
        return isReady;
    }

    /**
     * @see Ui
     */
    @Override
    synchronized public void process(String input) {
        if (input.contains("[ERROR]")) {
            String[] inputs = input.split("@@@");
            Platform.runLater(() -> GUIClient.getController().setError(inputs[1]));
        } else if (input.contains("[INIT]")) { /* if the string contains this prefix, it's an initialization string and it must be treated as such */
            String[] inputs = input.split("@@@");
            Client.setPlayerIndex((short) Integer.parseInt(inputs[1]));
            Client.setPlayersNumber((short) Integer.parseInt(inputs[2]));
            if (inputs.length > 3) {
                if (inputs.length == 5) {
                    Client.setGod(Integer.parseInt(inputs[4]));
                    Platform.runLater(() -> GUIClient.getController().switchScene("/Main.fxml"));
                    guiInitialized = true;
                } else
                    System.out.println("Your game's number is: " + Integer.parseInt(inputs[3]) + ". Keep it in case server goes down.");
            }
        } else if (input.contains("[CHOICE]")) {
            String[] inputs = input.split("@@@");
            if (inputs[1].equals("GODS")) {
                Client.setPlayersNumber((short) Integer.parseInt(inputs[2]));
                Platform.runLater(() -> loginController.firstPlayerGods());
            } else if (input.contains("POS")) {
                if (!guiInitialized) {
                    Platform.runLater(() -> GUIClient.getController().switchScene("/Main.fxml", true));
                    guiInitialized = true;
                }
                Platform.runLater(() -> GUIClient.getController().getStartingPositions(false));
            } else {
                Platform.runLater(() -> loginController.switchScene("/GodChoice.fxml", false));
                Platform.runLater(() -> ((GodChoiceController) GUIClient.getController()).parseGodChoice(input));
            }
        } else if (input.contains(ServerMessage.firstPlayer)) Platform.runLater(() -> loginController.firstPlayer());
        else if (input.equals(ServerMessage.reloadGameChoice)) Platform.runLater(() -> loginController.gameReload());
        else if (input.equals(ServerMessage.cellNotAvailable))
            Platform.runLater(() -> GUIClient.getController().getStartingPositions(true));
        else Platform.runLater(() -> loginController.setText(input));
    }

    /**
     * @see Ui
     */
    @Override
    public void waitForIP(Client client) {
        Platform.runLater(() -> loginController.waitForIP(client));
    }

    /**
     * @see Ui
     */
    @Override
    synchronized public void showTable(CellView[][] table) {
        while (!turnChanged) {
            try {
                wait();
            } catch (InterruptedException ignored) {
            }
        }
        Platform.runLater(() -> GUIClient.getController().updateTable(table));
    }

    /**
     * @see Ui
     */
    @Override
    synchronized public void processTurnChange(ClientState newState) {
        turnChanged = true;
        notify();
        Platform.runLater(() -> GUIClient.getController().setMove(newState));
    }

    /**
     * @see Ui
     */
    @Override
    public String nextLine(Scanner in) {
        while (!newValue) {
            Thread.onSpinWait();
        }
        newValue = false;
        return out;
    }

    /**
     * @see Ui
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        out = (String) evt.getNewValue();
        newValue = true;
    }
}