/*
 * Santorini
 * Copyright (C)  2020  Alessandro Villa and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Affero General Public License as
 *      published by the Free Software Foundation, either version 3 of the
 *      License, or (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Affero General Public License for more details.
 *
 *      You should have received a copy of the GNU Affero General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * E-mail contact addresses:
 * darklampz@gmail.com
 * alessandro17.villa@mail.polimi.it
 *
 */

package it.polimi.ingsw.Client.GUI;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Client.Ui;
import it.polimi.ingsw.Network.Messages.*;
import it.polimi.ingsw.Utility.Color;
import it.polimi.ingsw.View.CellView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.StageStyle;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static it.polimi.ingsw.Client.Client.*;

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


    @Override
    synchronized public void process(Message input) {
        if (input instanceof InitMessage) {
            InitMessage m = (InitMessage) input;
            setPlayerIndex(m.getPlayerIndex());
            setPlayersNumber(m.getSize());
            setTimer(m.getMoveTimer());
            System.out.println("Your game's number is: " + m.getGameIndex() + ". Keep it in case server goes down.");
            Client.setGod(m.getGod(Client.getPlayerIndex()));
            Client.setGods(m.getGod(0), m.getGod(1), m.getGod(2));
            if (!guiInitialized) {
                Platform.runLater(() -> GUIClient.getController().switchScene("/fxml/Main.fxml", true, null));
                guiInitialized = true;
            }
        } else if (input instanceof ErrorMessage) {
            Platform.runLater(() -> GUIClient.getController().setError((ErrorMessage) input));
        } else if (input instanceof MOTD) {
            if (((MOTD) input).persistenceEnabled()) process(ServerMessage.reloadGameChoice);
            else process(ServerMessage.welcomeNoPersistence);
        }

    }

    @Override
    synchronized public void process(String input) {
        String[] inputs = input.split("@@@");
        if (input.contains("[CHOICE]")) {
            if (inputs[1].equals("GODS")) {
                Client.setPlayersNumber((short) Integer.parseInt(inputs[2]));
                Platform.runLater(() -> loginController.firstPlayerGods());
            } else if (input.contains("POS")) {
                if (!guiInitialized) {
                    Platform.runLater(() -> GUIClient.getController().switchScene("/fxml/Main.fxml", true, null));
                    guiInitialized = true;
                }
                Platform.runLater(() -> GUIClient.getController().getStartingPositions(false, inputs));
            } else {
                Platform.runLater(() -> loginController.switchScene("/fxml/GodChoice.fxml", false, StageStyle.UNDECORATED));
                Platform.runLater(() -> ((GodChoiceController) GUIClient.getController()).parseGodChoice(input));
            }
        } else if (input.contains(ServerMessage.firstPlayer)) Platform.runLater(() -> loginController.firstPlayer());
        else if (input.equals(ServerMessage.reloadGameChoice)) Platform.runLater(() -> loginController.gameReload());
        else if (input.equals(ServerMessage.cellNotAvailable))
            Platform.runLater(() -> GUIClient.getController().getStartingPositions(true, inputs));
        else if (input.contains(ServerMessage.connClosed)) {
            if (inputs.length == 1) {
                System.err.println(Color.RESET + ServerMessage.connClosed);
                System.exit(0);
            } else {
                int pIndex = Integer.parseInt(inputs[1]);
                if (pIndex == Client.getPlayerIndex()) {
                    process(ServerMessage.gameLost);
                } else {
                    if (pIndex == 1) {
                        setGods(Client.getGods()[0], Client.getGods()[2], -1);
                        Client.setPlayerIndex(Client.getPlayerIndex() == 0 ? 0 : 1);
                    } else if (pIndex == 0) {
                        setGods(Client.getGods()[1], Client.getGods()[2], -1);
                        Client.setPlayerIndex(Client.getPlayerIndex() - 1);
                    } else {
                        setGods(Client.getGods()[0], Client.getGods()[1], -1);
                    }
                    process("Player " + getLastStateMessage().getName(pIndex) + " has lost!");
                    Client.setPlayersNumber(Client.getPlayersNumber() - 1);
                }
            }
        } else if (input.contains(ServerMessage.lastGod)) {
            Client.setGod(Integer.parseInt(inputs[1]));
            process("You're left with " + Client.completeGodList.get(Client.getGod()));
        } else if (!(input.equals(ServerMessage.firstBuilderPos)) && !(input.equals(ServerMessage.secondBuilderPos)))
            Platform.runLater(() -> GUIClient.getController().setText(input));
    }

    @Override
    public void waitForIP(Client client) {
        SplashScreen splashScreen = SplashScreen.getSplashScreen();
        if (splashScreen != null) {
            splashScreen.close();
        }
        Platform.runLater(() -> loginController.waitForIP(client));
    }

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

    @Override
    synchronized public void processTurnChange(ClientState newState) {
        turnChanged = true;
        Platform.runLater(() -> GUIClient.getController().setMove(newState));
        if (!(newState == ClientState.WIN || newState == ClientState.LOSE)) notify();
    }

    @Override
    public String nextLine(Scanner in) {
        while (true) {
            try {
                return queue.take();
            } catch (InterruptedException ignored) {
            }
        }
    }

    private final static BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        try {
            queue.put((String) evt.getNewValue());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}