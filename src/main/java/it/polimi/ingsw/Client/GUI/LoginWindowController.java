package it.polimi.ingsw.Client.GUI;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Client.ClientMain;
import it.polimi.ingsw.Network.Messages.ServerMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.InetAddress;
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
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setGraphic(null);
        alert.getDialogPane().setId("creditsPane");
        alert.getDialogPane().getStylesheets().add("css/Main.css");
        alert.setHeaderText(null);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(GUIClient.getAppIcon());
        alert.setContentText("\nFavicon made by Adib Sulthon (https://www.flaticon.com/authors/adib-sulthon)\n" +
                "Loser gif made by Dylan Morang (https://giphy.com/angchor)\n" +
                "Roman SD font by Steve Deffeyes (https://www.dafont.com/steve-deffeyes.d923)\n" +
                "Error icon by Eucalyp (https://www.flaticon.com/authors/eucalyp)\n" +
                "Dalek Pinpoint font by K-Type (https://www.dafont.com/k-type.d872)\n");
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
                textAreaMain.setText("Please enter the server's IP;port\nIf port is not provided, default will be used.");
                ipInput.setText(null);
                ipInput.setPromptText("Waiting for input..");
                flag = true;
            } else {
                InetAddress ip;
                try {
                    String[] a = ipInput.getText().split(";");
                    int port;
                    if (a.length == 1) {
                        port = Client.getPort();
                    } else port = Integer.parseInt(a[1]);
                    ip = InetAddress.getByName(a[0]);
                    Client.setIp(ip);
                    Client.setPort(port);
                    flag = true;
                    new Thread(client).start();
                    ipInput.clear();
                    connected = true;
                } catch (Exception e) {
                    textAreaMain.setText("Invalid address/port! Please try again.");
                }
            }
        } else {
            GUIClient.setOut(ipInput.getText());
            ipInput.clear();
        }
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
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setGraphic(null);
        dialog.getDialogPane().getStylesheets().add("css/Main.css");
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
        alert.setGraphic(null);
        alert.setContentText("Please choose whether you'd like to load a previous game or join a new one.");
        alert.initStyle(StageStyle.UNDECORATED);
        alert.getDialogPane().getStylesheets().add("css/Main.css");
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
                    textAreaMain.setText("Enter your name and press the connect button.");
                }
                break;
            }
        }
    }


    private Optional<String> result;

    void firstPlayerGods() {
        List<String> choices = new ArrayList<>(Client.completeGodList);
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Initializing game");
        dialog.setHeaderText("Gods choice");
        dialog.setGraphic(null);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.getDialogPane().getStylesheets().add("css/Main.css");
        dialog.setContentText(ServerMessage.nextChoice);
        result = dialog.showAndWait();
        getNextChoice(choices, dialog);
        newChoiceDialog(choices);
        if (GUI.getPlayersNumber() == 3) {
            newChoiceDialog(choices);
        }
    }

    private void newChoiceDialog(List<String> choices) {
        ChoiceDialog<String> dialog;
        choices.remove(result.get());
        dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setGraphic(null);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.getDialogPane().getStylesheets().add("css/Main.css");
        dialog.setTitle("Initializing game");
        dialog.setHeaderText("Gods choice");
        dialog.setContentText(ServerMessage.nextChoice);
        result = dialog.showAndWait();
        getNextChoice(choices, dialog);
    }

    private void getNextChoice(List<String> choices, ChoiceDialog<String> dialog) {
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
