package it.polimi.ingsw.Client.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

public class MainWindowController extends WindowController {
    @FXML
    Parent mainViewPane;
    @FXML
    TextArea textArea1;
    @FXML
    GridPane gridPaneMain;
    @FXML
    Button grid00;

    private static final MainWindowController instance = new MainWindowController();

    public static MainWindowController getInstance() {
        return instance;
    }

    @FXML
    void printProva(ActionEvent event) {
        event.consume();
        grid00.setText("PROVA");
    }


}
