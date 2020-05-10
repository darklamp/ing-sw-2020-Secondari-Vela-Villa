package it.polimi.ingsw.Client.GUI;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Utility.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class LoginWindowController extends WindowController {
    private boolean connected = false;

    @FXML
    TextField ipInput;
    @FXML
    Button ipSubmitBTN;
    @FXML
    TextArea textAreaMain;

    @FXML
    void setText(String s) {
        textAreaMain.setText(s);
    }

    private static Client client;

    @FXML
    private void print(ActionEvent event) {
        event.consume();
        if (!connected) {
            String ipAddress = ipInput.getText();
            String s;
            if (!Utils.isValidIP(ipAddress)) {
                s = "Invalid IP. Please enter a valid IP address then click Connect.";
                textAreaMain.setText(s);
            } else {
                s = "Connecting to: " + ipAddress + "...";
                textAreaMain.setText(s);
                client.run(ipAddress);
                connected = true;
                ipSubmitBTN.setText("Submit");
                //  MainWindowController.getInstance().switchScene("/Main.fxml");
            }
        } else {
            ((GUI) Client.getUi()).setOut(ipInput.getText());
        }
    }


    static void waitForIP(Client client){
        LoginWindowController.client = client;
    }

}
