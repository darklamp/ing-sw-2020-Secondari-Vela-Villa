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

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Network.Messages.ErrorMessage;
import it.polimi.ingsw.Network.Messages.ServerMessage;
import it.polimi.ingsw.View.CellView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.IOException;

public abstract class WindowController {

    static ImageCursor getCursor() {
        return cursor;
    }

    static void setCursor(ImageCursor cursor) {
        WindowController.cursor = cursor;
    }

    private static ImageCursor cursor;

    static ImageView getErrorIcon() {
        return errorIcon;
    }

    static void setErrorIcon(ImageView errorIcon) {
        WindowController.errorIcon = errorIcon;
    }

    private static ImageView errorIcon;


    /**
     * @param fxml file to be loaded
     * @see WindowController#switchScene(String, boolean)
     * Note: stage style set to null, so default will get used; fullScreen set to true.
     */
    void switchScene(@SuppressWarnings("SameParameterValue") String fxml) {
        switchScene(fxml, true);
    }

    /**
     * @see WindowController#switchScene(String, boolean, StageStyle)
     * Note: stage style set to null, so default will get used.
     * @param fxml       file to be loaded
     * @param fullScreen true if scene has to be set to fullscreen
     */
    void switchScene(@SuppressWarnings("SameParameterValue") String fxml, boolean fullScreen) {
        switchScene(fxml, fullScreen, null);
    }

    /**
     * Loads fxml file if present, then switches scene
     * @param fxml       file to be loaded
     * @param fullScreen true if scene has to be set to fullscreen
     * @param stageStyle style with which the stage has to be initialized
     */
    void switchScene(@SuppressWarnings("SameParameterValue") String fxml, boolean fullScreen, StageStyle stageStyle) {
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource(fxml));
        Parent root;
        try {
            root = loader.load();
            WindowController controller = loader.getController();
            GUIClient.setController(controller);
            Scene s = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(s);
            stage.setOnCloseRequest(we -> {
                System.out.println("Exiting...");
                stage.close();
                Platform.exit();
                System.exit(0);
            });
            stage.setResizable(true);
            if (fullScreen) stage.setFullScreenExitHint("Please use F11 to toggle fullscreen on/off.");
            s.setCursor(cursor);
            s.cursorProperty().addListener((obs, oldV, newV) -> s.setCursor(cursor));
            stage.initStyle(stageStyle == null ? StageStyle.DECORATED : stageStyle);
            GUIClient.getStage().close();
            GUIClient.setStage(stage);
            GUIClient.getStage().setScene(s);
            GUIClient.getStage().setFullScreen(fullScreen);
            stage.show();
            if (!fullScreen) {
                GUIClient.getStage().setX((Toolkit.getDefaultToolkit().getScreenSize().getWidth() - GUIClient.getStage().getScene().getWidth()) / 2);
                GUIClient.getStage().setY((Toolkit.getDefaultToolkit().getScreenSize().getHeight() - GUIClient.getStage().getScene().getHeight()) / 2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    abstract void setText(String s);

    /**
     * Takes a table representation from the server and proceeds to display it.
     *
     * @param table newly received table to be printed
     * @see MainWindowController#updateTable(CellView[][])
     */
    void updateTable(CellView[][] table) {
    }

    /**
     * Gets player builders' starting positions.
     *
     * @param cellOccupied if the server determined the selected cell is already occupied
     * @param inputs       eventual previously selected cells
     */
    void getStartingPositions(boolean cellOccupied, String[] inputs) {
    }

    /**
     * Reacts to ClientState news.
     *
     * @param newState
     */
    void setMove(ClientState newState) {
    }

    /**
     * Closes client application. Usually used with close buttons.
     *
     * @param event click event
     * @example {@link GodChoiceController#buttonClose}
     */
    @FXML
    void closeApp(ActionEvent event) {
        event.consume();
        Platform.exit();
        System.exit(0);
    }

    /**
     * Minimizes current stage.
     *
     * @param event click event
     * @example {@link GodChoiceController#buttonMinimize}
     */
    @FXML
    void minimize(ActionEvent event) {
        event.consume();
        GUIClient.getStage().setIconified(true);
    }


    /**
     * Spawns error dialog with content
     * @param input
     */
    void setError(String input) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Message");
        alert.setHeaderText("Error");
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setGraphic(WindowController.getErrorIcon());
        alert.setContentText(input == null ? "Generic error" : input);
        alert.setResizable(false);
        alert.showAndWait();
    }

    /**
     * Spawns error dialog with content
     * @param input
     */
    void setError(ErrorMessage input) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Message");
        alert.setHeaderText("Error");
        alert.setContentText(input.getContent());
        alert.setResizable(false);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setGraphic(WindowController.getErrorIcon());
        alert.showAndWait();
        if (input.equals(ServerMessage.gameLost) || input.equals(ServerMessage.abortMessage) || input.equals(ServerMessage.serverDown))
            Platform.exit();
    }

}
