package it.polimi.ingsw.Client.GUI;

import it.polimi.ingsw.Client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;

public class GodChoiceController extends WindowController {

    @FXML
    Button buttonForward, buttonBack, buttonSubmit;
    @FXML
    ImageView godCard;
    @FXML
    AnchorPane godChoicePane;

    private static Image god1, god2, god3;
    private static int i1, i2, i3;

    void parseGodChoice(String input) {
        String[] inputs = input.split("@@@");
        List<Integer> choices = new ArrayList<>();
        for (int i = 1; i < inputs.length; i++) {
            choices.add(Integer.parseInt(inputs[i]));
        }
        god1 = new Image("/images/GodCards/" + (choices.get(0) + 1) + ".png");
        i1 = choices.get(0);
        god2 = new Image("/images/GodCards/" + (choices.get(1) + 1) + ".png");
        i2 = choices.get(1);
        god3 = null;
        i3 = -1;
        if (choices.size() == 3) {
            god3 = new Image("/images/GodCards/" + (choices.get(2) + 1) + ".png");
            i3 = choices.get(2);
        }
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

    @FXML
    void back(ActionEvent event) {
        event.consume();
        if (godCard.getImage() == god1) {
            if (god3 != null) godCard.setImage(god3);
            else godCard.setImage(god2);
        } else if (godCard.getImage() == god2) {
            godCard.setImage(god1);
        } else godCard.setImage(god2);
    }

    @FXML
    void forward(ActionEvent event) {
        event.consume();
        if (godCard.getImage() == god1) {
            godCard.setImage(god2);
        } else if (godCard.getImage() == god2) {
            if (god3 != null) godCard.setImage(god3);
            else godCard.setImage(god1);
        } else godCard.setImage(god1);
    }

    @FXML
    void submit(ActionEvent event) {
        event.consume();
        int i = (godCard.getImage() == god1 ? i1 : (godCard.getImage() == god2 ? i2 : i3));
        Client.setGod(i);
        GUIClient.setOut(String.valueOf(i));
        buttonSubmit.setDisable(true);
    }


    @Override
    void setText(String s) {

    }
}
