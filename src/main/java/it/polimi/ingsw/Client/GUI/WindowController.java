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
     * {@link WindowController#switchScene(String, boolean)} (boolean = true)
     **/
    void switchScene(@SuppressWarnings("SameParameterValue") String fxml) {
        switchScene(fxml, true);
    }

    /**
     * Loads fxml file if present, then switches scene
     *
     * @param fxml       file to be loaded
     * @param fullScreen true if scene has to be set to fullscreen
     */
    void switchScene(@SuppressWarnings("SameParameterValue") String fxml, boolean fullScreen) {
        switchScene(fxml, fullScreen, null);
    }

    /**
     * Loads fxml file if present, then switches scene
     *
     * @param fxml       file to be loaded
     * @param fullScreen true if scene has to be set to fullscreen
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

    void updateTable(CellView[][] table) {
    }

    void getStartingPositions(boolean cellOccupied, String[] inputs) {
    }

    void setMove(ClientState newState) {
    }

    @FXML
    void closeApp(ActionEvent event) {
        event.consume();
        Platform.exit();
        System.exit(0);
    }

    @FXML
    void minimize(ActionEvent event) {
        event.consume();
        GUIClient.getStage().setIconified(true);
    }


    /**
     * Spawns error dialog with content
     *
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
     *
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
