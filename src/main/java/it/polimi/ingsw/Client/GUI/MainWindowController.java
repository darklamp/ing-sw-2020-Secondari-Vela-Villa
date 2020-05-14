package it.polimi.ingsw.Client.GUI;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.View.CellView;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
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
    Button cell00, cell01, cell02, cell03, cell04, cell10, cell11, cell12, cell13;
    @FXML
    Button cell14, cell20, cell21, cell22, cell23, cell24, cell30, cell31, cell32;
    @FXML
    Button cell33, cell34, cell40, cell41, cell42, cell43, cell44;

    private Button[][] cells = null;

   /* private static final MainWindowController instance = new MainWindowController();

    public static MainWindowController getInstance() {
        return instance;
    }*/

    @FXML
    void buttonClicked(ActionEvent event) {
        event.consume();
        Button b = (Button) event.getSource();
        if (cells == null) {
            cells = new Button[5][5];
            cells[0][0] = cell00;
            cells[0][1] = cell01;
            cells[0][2] = cell02;
            cells[0][3] = cell03;
            cells[0][4] = cell04;
            cells[1][0] = cell10;
            cells[1][1] = cell11;
            cells[1][2] = cell12;
            cells[1][3] = cell13;
            cells[1][4] = cell14;
            cells[2][0] = cell20;
            cells[2][1] = cell21;
            cells[2][2] = cell22;
            cells[2][3] = cell23;
            cells[2][4] = cell24;
            cells[3][0] = cell30;
            cells[3][1] = cell31;
            cells[3][2] = cell32;
            cells[3][3] = cell33;
            cells[3][4] = cell34;
            cells[4][0] = cell40;
            cells[4][1] = cell41;
            cells[4][2] = cell42;
            cells[4][3] = cell43;
            cells[4][4] = cell44;
        }
        int i, j = 0;
        boolean flag = false;
        for (i = 0; i < 5; i++) {
            for (j = 0; j < 5; j++) {
                if (b == cells[i][j]) {
                    flag = true;
                    break;
                }
            }
            if (flag) break;
        }
        if (Client.getState() == ClientState.INIT) {
            GUI.setOut(i + "," + j);
        }
    }

    void setText(String s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText("Message");
        alert.setContentText(s);
        alert.setResizable(false);
        alert.showAndWait();
    }

    @SuppressWarnings("SameParameterValue")
    void setText(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.setResizable(false);
        alert.showAndWait();
    }

    @Override
    void updateTable(CellView[][] table) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                cells[i][j].setText(table[i][j].getHeight().toString());
            }
        }
    }

    int positionedBuilders = 0;

    @Override
    void getStartingPositions(boolean cellOccupied) {
        String s;
        if (positionedBuilders == 2) {
            return;
        }
        if (!cellOccupied) {
            s = "Please select a cell on which to position your " + (positionedBuilders == 0 ? "first" : "second") + " builder.";
            positionedBuilders += 1;
        } else s = "Position already taken. Please choose a different one.";
        setText("Message", "Builders starting positions choice", s);

    }


    @FXML
    void setHovered(Event event) {
        event.consume();
        Button b = (Button) event.getSource();
        b.setOpacity(0.6);
    }

    @FXML
    void unsetHovered(Event event) {
        event.consume();
        Button b = (Button) event.getSource();
        b.setOpacity(1);
    }

}
