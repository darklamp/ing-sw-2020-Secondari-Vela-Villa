package it.polimi.ingsw.Client.GUI;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Network.ServerMessage;
import it.polimi.ingsw.Utility.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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


    static void waitForIP(Client client) {
        LoginWindowController.client = client;
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
                    ((GUI) Client.getUi()).setOut(String.valueOf(result.get()));
                    break;
                }
            } else {
                result = dialog.showAndWait();
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
        while (true) {
            if (result.isPresent()) {
                if (!choices.contains(result.get())) {
                    result = dialog.showAndWait();
                } else {
                    ((GUI) Client.getUi()).setOut(String.valueOf(Client.completeGodList.indexOf(result.get())));
                    break;
                }
            } else {
                result = dialog.showAndWait();
            }
        }
        choices.remove(result.get());
        dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Initializing game");
        dialog.setHeaderText("Gods choice");
        dialog.setContentText(ServerMessage.nextChoice);
        result = dialog.showAndWait();
        while (true) {
            if (result.isPresent()) {
                if (!choices.contains(result.get())) {
                    result = dialog.showAndWait();
                } else {
                    ((GUI) Client.getUi()).setOut(String.valueOf(Client.completeGodList.indexOf(result.get())));
                    break;
                }
            } else {
                result = dialog.showAndWait();
            }
        }
        if (GUI.getPlayersNumber() == 3) {
            choices.remove(result.get());
            dialog = new ChoiceDialog<>(choices.get(0), choices);
            dialog.setTitle("Initializing game");
            dialog.setHeaderText("Gods choice");
            dialog.setContentText(ServerMessage.nextChoice);
            result = dialog.showAndWait();
            while (true) {
                if (result.isPresent()) {
                    if (!choices.contains(result.get())) {
                        result = dialog.showAndWait();
                    } else {
                        ((GUI) Client.getUi()).setOut(String.valueOf(Client.completeGodList.indexOf(result.get())));
                        break;
                    }
                } else {
                    result = dialog.showAndWait();
                }
            }
        }
    }


    void parseGodChoice(String input) {
        String[] inputs = input.split("@@@");
        List<String> choices = new ArrayList<>();
        for (int i = 1; i < inputs.length; i++) {
            choices.add(Client.completeGodList.get(Integer.parseInt(inputs[i])));
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Initializing game");
        dialog.setHeaderText("God choice");
        dialog.setContentText(ServerMessage.nextChoice);
        Optional<String> result = dialog.showAndWait();
        while (true) {
            if (result.isPresent()) {
                if (!choices.contains(result.get())) {
                    result = dialog.showAndWait();
                } else {
                    ((GUI) Client.getUi()).setOut(String.valueOf(Client.completeGodList.indexOf(result.get())));
                    break;
                }
            } else {
                result = dialog.showAndWait();
            }
        }
    }


}
