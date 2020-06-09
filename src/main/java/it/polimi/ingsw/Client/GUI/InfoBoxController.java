package it.polimi.ingsw.Client.GUI;

import it.polimi.ingsw.Client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class InfoBoxController extends WindowController implements Initializable {

    @FXML
    ImageView godCard;
    @FXML
    ImageView builderImage;
    @FXML
    Text playerName;

    private int index;

    @FXML
    void back(ActionEvent event) {
        event.consume();
        index = (index == 0) ? (Client.getPlayersNumber() - 1) : (index - 1);
        godCard.setImage(GodChoiceController.getGodCard(Client.getGods()[index]));
        builderImage.setImage(MainWindowController.getBuilderImage(index + 1));
        playerName.setText(Client.getLastStateMessage().getName(index));
    }

    @FXML
    void forward(ActionEvent event) {
        event.consume();
        index = (Client.getPlayersNumber() == index + 1) ? 0 : (index + 1);
        godCard.setImage(GodChoiceController.getGodCard(Client.getGods()[index]));
        builderImage.setImage(MainWindowController.getBuilderImage(index + 1));
        playerName.setText(Client.getLastStateMessage().getName(index));
    }

    @FXML
    void close(ActionEvent event) {
        event.consume();
        Button b = (Button) (event.getSource());
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
    }
}
