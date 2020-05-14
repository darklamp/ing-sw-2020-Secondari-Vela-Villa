package it.polimi.ingsw.Client.GUI;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.View.CellView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public abstract class WindowController {
    void switchScene(@SuppressWarnings("SameParameterValue") String fxml) {
        {

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
    }

    abstract void setText(String s);

    void updateTable(CellView[][] table) {
    }

    void getStartingPositions(boolean cellOccupied) {
    }

    void setMove(ClientState newState) {
    }

    ;;
}
