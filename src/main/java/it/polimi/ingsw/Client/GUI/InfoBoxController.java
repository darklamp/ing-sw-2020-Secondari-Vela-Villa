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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controls in-game InfoBox.
 *
 * @see MainWindowController
 */
public class InfoBoxController extends WindowController implements Initializable {

    @FXML
    ImageView godCard;
    @FXML
    ImageView builderImage;
    @FXML
    Text playerName;
    @FXML
    Button closeButton, buttonForward, buttonBack;

    private int index;
    private final static Image btnBack = new Image("/images/btn_back.png");
    private final static Image btnForward = new Image("/images/btn_fwd.png");


    /**
     * Back button.
     *
     * @param event click event
     */
    @FXML
    void back(ActionEvent event) {
        event.consume();
        index = (index == 0) ? (Client.getPlayersNumber() - 1) : (index - 1);
        godCard.setImage(GodChoiceController.getGodCard(Client.getGods()[index]));
        builderImage.setImage(MainWindowController.getBuilderImage(index + 1));
        playerName.setText(Client.getLastStateMessage().getName(index));
    }

    /**
     * Fwd button.
     *
     * @param event click event
     */
    @FXML
    void forward(ActionEvent event) {
        event.consume();
        index = (Client.getPlayersNumber() == index + 1) ? 0 : (index + 1);
        godCard.setImage(GodChoiceController.getGodCard(Client.getGods()[index]));
        builderImage.setImage(MainWindowController.getBuilderImage(index + 1));
        playerName.setText(Client.getLastStateMessage().getName(index));
    }

    /**
     * Close button.
     *
     * @param event click event
     */
    @FXML
    void close(ActionEvent event) {
        event.consume();
        Button b = (Button) (event.getSource());
        GUIClient.getStage().getScene().getRoot().setEffect(null);
        ((Stage) (b.getParent().getScene().getWindow())).close();
    }


    @Override
    void setText(String s) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        index = Client.getPlayerIndex();
        godCard.setImage(GodChoiceController.getGodCard(Client.getGod()));
        builderImage.setImage(MainWindowController.getBuilderImage(index + 1));
        playerName.setText(Client.getLastStateMessage().getName(index));
        /* NB: we can't load fonts within css stylesheets because JavaFX breaks if there's a whitespace in the path
           (known JavaFX bug apparently)
         */
        playerName.setFont(GUIClient.getDefaultFont());
        closeButton.setFont(GUIClient.getDefaultFont());
        buttonBack.setGraphic(new ImageView(btnBack));
        buttonForward.setGraphic(new ImageView(btnForward));
    }
}
