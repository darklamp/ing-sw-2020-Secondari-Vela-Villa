/*package it.polimi.ingsw.Client;

import it.polimi.ingsw.Utility.Utils;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class GUIClient extends Application {

    @FXML TextField ipInput;
    @FXML Button ipSubmitBTN;
    @FXML
    TextArea textAreaMain;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/Main.fxml"));
        primaryStage.setTitle("Santorini Client");
        primaryStage.setScene(new Scene(root,400,150));
        primaryStage.show();
    }

    @FXML
    private void printHelloWorld(ActionEvent event){
        event.consume();
        String ipAddress = ipInput.getText();
        String s = null;
        if (!Utils.isValidIP(ipAddress)) s = "Invalid IP. Please enter a valid IP address then click Connect.";
        else {
            s = "Connecting to: " + ipInput.getText() + "...";
            textAreaMain.setText(s);
            try {
                connect(ipAddress);
            } catch (IOException e) {
                s = "Connection refused. The server is down or full.\nPlease try again.";
            }
            finally {
                textAreaMain.setText(s);
            }
        }
    }
    public static void main(String[] args) {
        launch(args);

    }

    private void connect(String ip) throws IOException {
        LineClient client = new LineClient(ip, 1337);
        client.startClient();
    }
}
*/