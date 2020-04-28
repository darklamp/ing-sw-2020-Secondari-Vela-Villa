package it.polimi.ingsw.Client.GUI;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Utility.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;

public class GUIController {

    @FXML
    TextField ipInput;
    @FXML
    Button ipSubmitBTN;
    @FXML
    TextArea textAreaMain;

    @FXML
    void setText(String s){
        textAreaMain.setText(s);
    }

    private static Client client;

    @FXML
    private void print(ActionEvent event) throws IOException {
        event.consume();
        String ipAddress = ipInput.getText();
        String s = null;
        if (!Utils.isValidIP(ipAddress)) s = "Invalid IP. Please enter a valid IP address then click Connect.";
        else {
            s = "Connecting to: " + ipInput.getText() + "...";
            textAreaMain.setText(s);
            client.run(s);
        }
    }

    static void waitForIP(Client client){
        GUIController.client = client;
    }

}
