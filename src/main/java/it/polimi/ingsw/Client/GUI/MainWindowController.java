package it.polimi.ingsw.Client.GUI;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Utility.Pair;
import it.polimi.ingsw.View.CellView;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Box;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

import static it.polimi.ingsw.Client.ClientState.INIT;

public class MainWindowController extends WindowController implements Initializable {
    @FXML
    Parent mainViewPane;
    @FXML
    GridPane buildingPartsPane;
    @FXML
    TextArea textArea1;
    @FXML
    GridPane gridPaneMain;
    @FXML
    Box testBox1;
    @FXML
    Group cell00, cell01, cell02, cell03, cell04, cell10, cell11, cell12, cell13;
    @FXML
    Group cell14, cell20, cell21, cell22, cell23, cell24, cell30, cell31, cell32;
    @FXML
    Group cell33, cell34, cell40, cell41, cell42, cell43, cell44;


    private static final DataFormat builder = new DataFormat("builder");
    private static final DataFormat building = new DataFormat("building");
    private boolean initialized = false;
    private static Image builderImage1;

    {
        try {
            builderImage1 = new Image(new FileInputStream(new File("./").getAbsolutePath().replace(".", "") + "src/main/resources/images/builder1.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Image builderImage2;

    {
        try {
            builderImage2 = new Image(new FileInputStream(new File("./").getAbsolutePath().replace(".", "") + "src/main/resources/images/builder2.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Image builderImage3;

    {
        try {
            builderImage3 = new Image(new FileInputStream(new File("./").getAbsolutePath().replace(".", "") + "src/main/resources/images/builder3.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Image baseBuildingImage;

    {
        try {
            baseBuildingImage = new Image(new FileInputStream(new File("./").getAbsolutePath().replace(".", "") + "src/main/resources/images/buildingBase.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Image middleBuildingImage;

    {
        try {
            middleBuildingImage = new Image(new FileInputStream(new File("./").getAbsolutePath().replace(".", "") + "src/main/resources/images/buildingMiddle.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (Node n : gridPaneMain.getChildren()) {
            initGroup((Group) n);
        }
    }

    @FXML
    void buttonClicked(Event event) {
        event.consume();
        if (Client.getState() != INIT) return;
        Button b = (Button) event.getSource();
        Group g = (Group) b.getParent();
        int i = getRow(g);
        int j = getColumn(g);
        System.out.println(i + "   " + j);
        GUI.setOut(i + "," + j);
    }

    @Override
    void setMove(ClientState s) {
        String s1 = "";
        if (!initialized) {
            for (Node n : gridPaneMain.getChildren()) {
                ((Button) ((Group) n).getChildren().get(0)).setOnAction(null);
            }
            if (GUI.getPlayersNumber() == 2) {

            }
            initialized = true;
        }
        switch (s) {
            case MOVE -> s1 = "It's your turn! Please make a move.";
            case BUILD -> s1 = "You have to: BUILD";
        }
        textArea1.setText(s1);
    }

    @FXML
    void builderGrab(MouseEvent event) {
        System.out.println("[DEBUG]" + new Throwable().getStackTrace()[0].getMethodName() + event.getSource() + " Entered drag");
        ImageView i = (ImageView) event.getSource();
        Dragboard db = i.startDragAndDrop(TransferMode.ANY);
        Image image = (GUI.getPlayerIndex() == 0 ? builderImage2 : (GUI.getPlayerIndex() == 1 ? builderImage1 : builderImage3)); //TODO capire perchè sono invertiti
        db.setDragView(image, 155, 155);
        ClipboardContent content = new ClipboardContent();
        int row = getRow(i.getParent());
        int col = getColumn(i.getParent());
        content.put(builder, new Pair(row, col));
        db.setContent(content);
        event.consume();
    }

    private static int getRow(Node node) {
        int row;
        try {
            row = GridPane.getRowIndex(node);
        } catch (Exception e) {
            row = 0;
        }
        return row;
    }

    private static int getColumn(Node node) {
        int col;
        try {
            col = GridPane.getColumnIndex(node);
        } catch (Exception e) {
            col = 0;
        }
        return col;
    }

    @FXML
    void dragReleased(DragEvent event) {
        System.out.println("[DEBUG]" + new Throwable().getStackTrace()[0].getMethodName() + event.getTarget() + " Targeted from drag");
        Dragboard db = event.getDragboard();
        boolean success = false;
        boolean isBuilding = false;
        if (db.hasContent(builder) || db.hasContent(building)) {
            if (db.hasContent(building)) isBuilding = true;
            success = true;
        }
        event.setDropCompleted(success);

        Pair pair = (Pair) db.getContent(isBuilding ? building : builder);
        Group target = null;
        try {
            target = (Group) ((Button) event.getTarget()).getParent();
        } catch (ClassCastException e) { // should only happen when event.getTarget() is an ImageView and not a button, aka when there's a builder on the cell I'm trying to move to
            if (!(event.getTarget() instanceof ImageView)) e.printStackTrace();
            else target = (Group) ((ImageView) event.getTarget()).getParent();
        }
        int i = getRow(target);
        int j = getColumn(target);
        ImageView source = (ImageView) ((Group) gridPaneMain.getChildren().get(pair.getFirst() * 5 + pair.getSecond())).getChildren().get(1);
        switch (Client.getState()) {
            case MOVE -> GUI.setOut(i + "," + j + "," + ((Integer.parseInt(source.getId()) % 2) + 1));
        }
        event.consume();
    }

    void setText(String s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText("Message");
        alert.setContentText(s);
        alert.setResizable(false);
        alert.showAndWait();
    }

    @SuppressWarnings("SameParameterValue")
    void setText(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.setResizable(false);
        alert.showAndWait();
    }

    @Override
    void updateTable(CellView[][] table) {
        // gridPaneMain.getChildren().clear();
        ImageView a = new ImageView(builderImage1);
        a.setPreserveRatio(true);
        a.setFitHeight(155);
        buildingPartsPane.add(a, 0, 0);
        a.toFront();
        a.setOnDragDetected(this::builderGrab);
        int fb1 = 0, fb2 = 2, fb3 = 4;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                switch (table[i][j].getHeight()) {
                    case NONE -> addBuildingToCell(null, i, j);
                    case BASE -> addBuildingToCell(baseBuildingImage, i, j);
                    case MIDDLE -> addBuildingToCell(middleBuildingImage, i, j);
                    case TOP -> addBuildingToCell(middleBuildingImage, i, j); //TODO add image
                    case DOME -> addBuildingToCell(middleBuildingImage, i, j); //TODO add image
                }
                switch (table[i][j].getPlayer()) {
                    case 0 -> {
                        addBuilderToCell(builderImage1, i, j, fb1);
                        fb1 = 1;
                    }
                    case 1 -> {
                        addBuilderToCell(builderImage2, i, j, fb2);
                        fb2 = 3;
                    }
                    case 2 -> {
                        addBuilderToCell(builderImage3, i, j, fb3);
                        fb3 = 5;
                    }
                    case -1 -> {
                        addBuilderToCell(null, i, j, 0);
                    }
                }
            }
        }
    }

    private void addBuildingToCell(Image image, int row, int column) {
        if (image == null) {
            Group g = (Group) gridPaneMain.getChildren().get(row * 5 + column);
            ((Button) g.getChildren().get(0)).setGraphic(null);
        } else {
            ImageView b = new ImageView(image);
            b.setFitHeight(155);
            b.setFitWidth(155);
            b.setScaleX(0.9);
            b.setScaleY(0.9);
            b.setOpacity(0.8);
            ((Button) ((Group) gridPaneMain.getChildren().get(row * 5 + column)).getChildren().get(0)).setGraphic(b);
            b.toBack();
        }
    }

    private void addBuilderToCell(Image image, int row, int column, int firstBuilder) {
        if (image == null) {
            Group g = (Group) gridPaneMain.getChildren().get(row * 5 + column);
            g.getChildren().removeIf(i -> i instanceof ImageView);
        } else {
            ImageView b = new ImageView(image);
            b.setId(String.valueOf(firstBuilder));
            b.setFitHeight(155);
            b.setFitWidth(155);
            if (firstBuilder / (GUI.getPlayerIndex() + 1) == 1 || (GUI.getPlayerIndex() == 0 && firstBuilder == 1)) {
                b.setOnDragDetected(this::builderGrab);
                b.setOnMouseEntered(this::setHovered);
                b.setOnMouseExited(this::unsetHovered);
            }
            b.setScaleX(1.65);
            b.setScaleY(1.65);
            b.setScaleZ(1.65);
            ((Group) gridPaneMain.getChildren().get(row * 5 + column)).getChildren().add(b);
            b.toFront();
        }

    }

    private int positionedBuilders = -1;
    private boolean flagInvalidPos = false;

    @Override
    void getStartingPositions(boolean cellOccupied) {
        String s;
        if (positionedBuilders == 2) {
            return;
        }
        if (!cellOccupied) {
            if (!flagInvalidPos) {
                s = "Please select a cell on which to position your " + (positionedBuilders == -1 ? "first" : "second") + " builder.";
                positionedBuilders += 1;
                setText("Message", "Builders starting positions choice", s);
            }
            flagInvalidPos = false;
        } else {
            s = "Position already taken. Please choose a different one.";
            flagInvalidPos = true;
            setText("Message", "Builders starting positions choice", s);
        }

    }

    @FXML
    void dragOver(DragEvent event) {
        event.acceptTransferModes(TransferMode.ANY);
        event.consume();
    }


    @FXML
    void setHovered(Event event) {
        event.consume();
        ImageView b = (ImageView) event.getSource();
        b.setEffect(new Glow(2.8));
        b.setScaleX(1.85);
        b.setScaleY(1.85);
        b.setScaleZ(1.85);
    }

    @FXML
    void unsetHovered(Event event) {
        event.consume();
        ImageView b = (ImageView) event.getSource();
        b.setEffect(null);
        b.setScaleX(1.75);
        b.setScaleY(1.75);
        b.setScaleZ(1.75);
    }


    void initGroup(Group g) {
        g.setOnDragDropped(this::dragReleased);
        g.setOnDragOver(this::dragOver);
        Button b = new Button();
        b.setAlignment(Pos.CENTER);
        b.setOnAction(this::buttonClicked);
        b.setMaxHeight(155);
        b.setMaxWidth(155);
        g.getChildren().add(b);
        b.setVisible(true);
        b.setGraphic(new ImageView(builderImage1));
        ((ImageView) (b.getGraphic())).setFitWidth(155);
        ((ImageView) (b.getGraphic())).setFitHeight(155);
        b.getGraphic().setVisible(false);
        b.toFront();
    }

}
