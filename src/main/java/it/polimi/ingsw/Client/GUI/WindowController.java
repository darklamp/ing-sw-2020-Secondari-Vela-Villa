package it.polimi.ingsw.Client.GUI;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public abstract class WindowController {
    void switchScene(String fxml) {
        {

            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource(fxml));
            Parent root;
            try {
                root = loader.load();
                if (fxml.equals("/Login.fxml")) {
                    LoginWindowController controller = loader.getController();
                } else if (fxml.equals("/Main.fxml")) {
                    MainWindowController controller = loader.getController();
                }
                GUIClient.getStage().setScene(new Scene(root));
                GUIClient.getStage().setFullScreen(true);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
