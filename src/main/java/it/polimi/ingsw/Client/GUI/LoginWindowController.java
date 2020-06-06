package it.polimi.ingsw.Client.GUI;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Client.ClientMain;
import it.polimi.ingsw.Network.ServerMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "JavaDoc"})
public class LoginWindowController extends WindowController {
    private boolean connected = false;

    @FXML
    TextField ipInput;
    @FXML
    Button ipSubmitBTN;
    @FXML
    TextArea textAreaMain;
    @FXML
    Button creditsButton;

    @FXML
    void setText(String s) {
        textAreaMain.setText(s);
    }

    @FXML
    private void showCredits() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Credits");
        alert.setHeaderText(null);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(GUIClient.getAppIcon());
        alert.setContentText("""
                                Favicon made by Adib Sulthon (https://www.flaticon.com/authors/adib-sulthon).
                                Loser gif made by Dylan Morang (https://giphy.com/angchor).
                """);
        alert.setResizable(false);
        alert.showAndWait();
    }

    private static Client client;
    private boolean flag = false;

    @FXML
    private void print(ActionEvent event) {
        event.consume();
        if (!connected) {
            if (ClientMain.validIP()) {
                new Thread(client).start();
                connected = true;
            } else if (!flag) {
                textAreaMain.setText("Please enter the server's IP address / hostname.");
                ipInput.setText(null);
                ipInput.setPromptText("Waiting for input..");
                flag = true;
            } else {
                InetAddress ip;
                try {
                    ip = InetAddress.getByName(ipInput.getText());
                    Client.setIp(ip);
                    flag = true;
                    new Thread(client).start();
                    connected = true;
                } catch (UnknownHostException e) {
                    textAreaMain.setText("Invalid IP! Please try again.");
                }
            }
        } else GUIClient.setOut(ipInput.getText());
    }


    void waitForIP(Client client) {
        LoginWindowController.client = client;
        ipSubmitBTN.setDefaultButton(true);
        print(new ActionEvent());
    }

    void firstPlayer() {
        List<Integer> choices = new ArrayList<>();
        choices.add(2);
        choices.add(3);
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(2, choices);
        dialog.setTitle("Initializing game");
        dialog.setHeaderText("Player number choice");
        dialog.setContentText(ServerMessage.firstPlayer);
        Optional<Integer> result = dialog.showAndWait();
        while (true) {
            if (result.isPresent()) {
                if (result.get() < 2 || result.get() > 3) {
                    result = dialog.showAndWait();
                } else {
                    GUIClient.setOut(String.valueOf(result.get()));
                    break;
                }
            } else {
                result = dialog.showAndWait();
            }
        }
    }

    void gameReload() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Please choose whether you'd like to load a previous game or join a new one.");
        ButtonType b1 = new ButtonType("Reload");
        ButtonType b2 = new ButtonType("Join");
        alert.getButtonTypes().setAll(b2, b1);
        while (true) {
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == b1) {
                    GUIClient.setOut("R");
                    ipInput.setText(null);
                } else {
                    textAreaMain.setText("Please enter your name and press the button.");
                }
                break;
            }
        }
    }

    void firstPlayerGods() {
        List<String> choices = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            choices.add(Client.completeGodList.get(i));
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Initializing game");
        dialog.setHeaderText("Gods choice");
        dialog.setContentText(ServerMessage.nextChoice);
        Optional<String> result = dialog.showAndWait();
        getNextChoice(result, choices, dialog);
        newChoiceDialog(choices, result);
        if (GUI.getPlayersNumber() == 3) {
            newChoiceDialog(choices, result);
        }
    }

    private void newChoiceDialog(List<String> choices, Optional<String> result) {
        ChoiceDialog<String> dialog;
        choices.remove(result.get());
        dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Initializing game");
        dialog.setHeaderText("Gods choice");
        dialog.setContentText(ServerMessage.nextChoice);
        result = dialog.showAndWait();
        getNextChoice(result, choices, dialog);
    }

    private void getNextChoice(Optional<String> result, List<String> choices, ChoiceDialog<String> dialog) {
        while (true) {
            if (result.isPresent()) {
                if (!choices.contains(result.get())) {
                    result = dialog.showAndWait();
                } else {
                    GUIClient.setOut(String.valueOf(Client.completeGodList.indexOf(result.get()) + 1));
                    break;
                }
            } else {
                result = dialog.showAndWait();
            }
        }
    }

}
