package it.polimi.ingsw.Client.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainWindowController extends WindowController {
    @FXML
    Parent mainViewPane;
    @FXML
    TextArea textArea1;
    @FXML
    GridPane gridPaneMain;
    @FXML
    Button cell00, cell01, cell02, cell03, cell04, cell10, cell11, cell12, cell13;
    @FXML
    Button cell14, cell20, cell21, cell22, cell23, cell24, cell30, cell31, cell32;
    @FXML
    Button cell33, cell34, cell40, cell41, cell42, cell43, cell44;

    private static final MainWindowController instance = new MainWindowController();

    public static MainWindowController getInstance() {
        return instance;
    }

    @FXML
    void printProva(ActionEvent event) {
        event.consume();
        Button b = (Button) event.getSource();
        if (b.getText().equals("PROVA")) {
            b.setText("ASD");
        } else b.setText("PROVA");
    }

    void setText(String s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText("ASD");
        alert.setContentText(s);
        alert.setResizable(false);
        alert.showAndWait();
    }

    void chooseGod() {
        List<String> choices = new ArrayList<>();
        choices.add("a");
        choices.add("b");
        choices.add("c");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("b", choices);
        dialog.setTitle("Initializing game");
        dialog.setHeaderText("God choice");
        dialog.setContentText("Please choose a god:");
        Optional<String> result = dialog.showAndWait();
        while (true) {
            if (result.isPresent()) {
                System.out.println("Your choice: " + result.get());
                break;
            } else {
                result = dialog.showAndWait();
            }
        }
    }

}
