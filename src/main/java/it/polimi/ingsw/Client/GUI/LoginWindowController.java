package it.polimi.ingsw.Client.GUI;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Network.ServerMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

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
    Button creditsButton, buttonForward, buttonBack, buttonSubmit;
    @FXML
    ImageView godCard;
    @FXML
    AnchorPane godChoicePane;

    private static Image god1, god2, god3;

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
                """);
        alert.setResizable(false);
        alert.showAndWait();
    }

    private static Client client;

    @FXML
    private void print(ActionEvent event) {
        event.consume();
        if (!connected) {
           /* String ipAddress = Client.getIP().toString();
            String s;
            s = "Connecting to: " + ipAddress + "...";
            textAreaMain.setText(s);*/
            new Thread(client).start();
            connected = true;
        } else GUIClient.setOut(ipInput.getText());
    }


    void waitForIP(Client client) {
        LoginWindowController.client = client;
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
        alert.getButtonTypes().setAll(b1, b2);
        while (true) {
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == b1) {
                    GUIClient.setOut("R");
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
        while (true) {
            if (result.isPresent()) {
                if (!choices.contains(result.get())) {
                    result = dialog.showAndWait();
                } else {
                    GUIClient.setOut(String.valueOf(Client.completeGodList.indexOf(result.get())));
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
                    GUIClient.setOut(String.valueOf(Client.completeGodList.indexOf(result.get())));
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
                        GUIClient.setOut(String.valueOf(Client.completeGodList.indexOf(result.get())));
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
        List<Integer> choices = new ArrayList<>();
        for (int i = 1; i < inputs.length; i++) {
            choices.add(Integer.parseInt(inputs[i]));
        }
        switchScene("/GodChoice.fxml");
        GUIClient.getStage().setFullScreen(false);
        god1 = new Image("/images/GodCards/" + choices.get(0) + ".png");
        god2 = new Image("/images/GodCards/" + choices.get(1) + ".png");
        god3 = null;
        if (choices.size() == 3) god3 = new Image("/images/GodCards/" + choices.get(2) + ".png");
        godCard.setImage(god1);
        /*
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
                    GUIClient.setOut(String.valueOf(Client.completeGodList.indexOf(result.get())));
                    break;
                }
            } else {
                result = dialog.showAndWait();
            }
        }*/
    }


}
