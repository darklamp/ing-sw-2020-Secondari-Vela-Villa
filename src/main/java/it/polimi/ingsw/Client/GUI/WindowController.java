package it.polimi.ingsw.Client.GUI;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Network.Messages.ErrorMessage;
import it.polimi.ingsw.Network.Messages.ServerMessage;
import it.polimi.ingsw.View.CellView;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;

import java.io.IOException;

public abstract class WindowController {

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
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource(fxml));
        Parent root;
        try {
            root = loader.load();
            WindowController controller = loader.getController();
            GUIClient.setController(controller);
            Scene s = new Scene(root);
            GUIClient.getStage().setScene(s);
            GUIClient.getStage().setFullScreen(fullScreen);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    abstract void setText(String s);

    void updateTable(CellView[][] table) {
    }

    void getStartingPositions(boolean cellOccupied) {
    }

    void setMove(ClientState newState) {
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
        alert.showAndWait();
        if (input.equals(ServerMessage.gameLost) || input.equals(ServerMessage.abortMessage) || input.equals(ServerMessage.serverDown))
            Platform.exit();
    }

}
