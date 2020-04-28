package it.polimi.ingsw.Client.GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindowController {
    @FXML
    Parent mainViewPane;

    private static final MainWindowController instance = new MainWindowController();

    public static MainWindowController getInstance() {
        return instance;
    }

    void changeScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
        Parent pane = loader.load();
        Stage stage = GUIClient.getStage();
        stage.getScene().setRoot(pane);
        stage.show();
    }
}
