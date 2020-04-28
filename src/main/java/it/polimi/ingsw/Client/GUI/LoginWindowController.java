package it.polimi.ingsw.Client.GUI;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Utility.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginWindowController {

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
        String s;
        if (!Utils.isValidIP(ipAddress)) s = "Invalid IP. Please enter a valid IP address then click Connect.";
        else {
            s = "Connecting to: " + ipAddress + "...";
            textAreaMain.setText(s);
            client.run(ipAddress);
            //MainWindowController.getInstance().changeScene();
        }
    }

    static void waitForIP(Client client){
        LoginWindowController.client = client;
    }

}
