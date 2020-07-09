/*
 * Santorini
 * Copyright (C)  2020  Alessandro Villa and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Affero General Public License as
 *      published by the Free Software Foundation, either version 3 of the
 *      License, or (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Affero General Public License for more details.
 *
 *      You should have received a copy of the GNU Affero General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * E-mail contact addresses:
 * darklampz@gmail.com
 * alessandro17.villa@mail.polimi.it
 *
 */

package it.polimi.ingsw.Client.GUI;

import it.polimi.ingsw.Client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controls god choice after login.
 */
public class GodChoiceController extends WindowController implements Initializable {

    @FXML
    Button buttonForward, buttonBack, buttonSubmit, buttonClose, buttonMinimize;
    @FXML
    ImageView godCard;
    @FXML
    AnchorPane godChoicePane;

    private static Image god1, god2, god3;
    private static final Image[] godCards = new Image[Client.completeGodList.size()];
    private final static Image btnBack = new Image("/images/btn_back.png");
    private final static Image btnForward = new Image("/images/btn_fwd.png");


    static {
        for (int i = 0; i < Client.completeGodList.size(); i++) {
            if (Client.verbose()) System.out.println("[DEBUG] Loaded card: " + Client.completeGodList.get(i) + ".png");
            godCards[i] = new Image("/images/GodCards/" + Client.completeGodList.get(i) + ".png");
        }
    }

    private static int i1, i2, i3;

    /**
     * Returns the player's god card
     *
     * @param i player index (0..2)
     * @return god card as image if index valid, else defaults to returning godCards[0]
     */
    static Image getGodCard(int i) {
        return (i < godCards.length && i >= 0) ? godCards[i] : godCards[0];
    }

    /**
     * Parses player god choices to create the view.
     *
     * @param input eventually contains other player's previous choices
     */
    void parseGodChoice(String input) {
        String[] inputs = input.split("@@@");
        List<Integer> choices = new ArrayList<>();
        for (int i = 1; i < inputs.length; i++) {
            choices.add(Integer.parseInt(inputs[i]));
        }
        god1 = new Image("/images/GodCards/" + (Client.completeGodList.get(choices.get(0))) + ".png");
        i1 = choices.get(0);
        god2 = new Image("/images/GodCards/" + (Client.completeGodList.get(choices.get(1))) + ".png");
        i2 = choices.get(1);
        god3 = null;
        i3 = -1;
        if (choices.size() == 3) {
            god3 = new Image("/images/GodCards/" + (Client.completeGodList.get(choices.get(2))) + ".png");
            i3 = choices.get(2);
        }
        godCard.setImage(god1);
        GUIClient.getStage().getScene().setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.RIGHT) {
                forward(new ActionEvent());
            } else if (e.getCode() == KeyCode.LEFT) {
                back(new ActionEvent());
            }
        });
    }

    /**
     * Arrow back
     *
     * @param event click event
     */
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

    /**
     * Arrow fwd
     *
     * @param event click event
     */
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


    /**
     * Submit button. Sends god choice to server and disables buttons until game starts.
     *
     * @param event click event
     */
    @FXML
    void submit(ActionEvent event) {
        event.consume();
        int i = (godCard.getImage() == god1 ? i1 : (godCard.getImage() == god2 ? i2 : i3));
        Client.setGod(i);
        GUIClient.setOut(String.valueOf(i + 1));
        buttonSubmit.setDisable(true);
        buttonBack.setDisable(true);
        buttonForward.setDisable(true);
        GUIClient.getStage().getScene().setOnKeyReleased(null);
    }


    @Override
    void setText(String s) {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buttonBack.setGraphic(new ImageView(btnBack));
        buttonForward.setGraphic(new ImageView(btnForward));
        Font font = (GUIClient.getDefaultFont());
        buttonSubmit.setFont(font);
        buttonMinimize.setFont(font);
        buttonClose.setFont(font);
    }
}
