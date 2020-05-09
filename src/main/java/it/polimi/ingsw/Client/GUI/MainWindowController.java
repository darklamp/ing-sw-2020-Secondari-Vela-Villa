package it.polimi.ingsw.Client.GUI;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

public class MainWindowController extends WindowController {
    @FXML
    Parent mainViewPane;
    @FXML
    TextArea textArea1;
    @FXML
    GridPane gridPaneMain;

    private static final MainWindowController instance = new MainWindowController();

    public static MainWindowController getInstance() {
        return instance;
    }


}
