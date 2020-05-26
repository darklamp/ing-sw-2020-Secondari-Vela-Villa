package it.polimi.ingsw.Client.GUI;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.View.CellView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;

import java.io.IOException;

public abstract class WindowController {
    void switchScene(@SuppressWarnings("SameParameterValue") String fxml) {
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource(fxml));
        Parent root;
        try {
            root = loader.load();
            WindowController controller = loader.getController();
            GUIClient.setController(controller);
            GUIClient.getStage().setScene(new Scene(root));
            GUIClient.getStage().setFullScreen(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void switchScene(@SuppressWarnings("SameParameterValue") String fxml, boolean fullScreen) {
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource(fxml));
        Parent root;
        try {
            root = loader.load();
            WindowController controller = loader.getController();
            GUIClient.setController(controller);
            GUIClient.getStage().setScene(new Scene(root));
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

    void setError(String input) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Message");
        alert.setHeaderText("Error");
        alert.setContentText(input == null ? "Generic error" : input);
        alert.setResizable(false);
        alert.showAndWait();
    }
}
