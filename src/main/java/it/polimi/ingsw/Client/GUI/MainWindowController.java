package it.polimi.ingsw.Client.GUI;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Utility.Pair;
import it.polimi.ingsw.View.CellView;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
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
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static it.polimi.ingsw.Client.ClientState.*;

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
    StackPane cell00, cell01, cell02, cell03, cell04, cell10, cell11, cell12, cell13;
    @FXML
    StackPane cell14, cell20, cell21, cell22, cell23, cell24, cell30, cell31, cell32;
    @FXML
    StackPane cell33, cell34, cell40, cell41, cell42, cell43, cell44;


    private static final DataFormat builder = new DataFormat("builder");
    private static final DataFormat building = new DataFormat("building");

    private static ImageView selected = null;
    /**
     * keeps track of selected builder
     **/

    private boolean initialized = false;
    private static final Image builderImage1 = new Image("/images/builder1.png");
    private static final Image builderImage2 = new Image("/images/builder2.png");
    private static final Image builderImage3 = new Image("/images/builder3.png");


    private final static Image baseBuildingImage = new Image("/images/buildingBase.png");
    private final static Image middleBuildingImage = new Image("/images/buildingMiddle.png");


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (Node n : gridPaneMain.getChildren()) {
            initStackPane((StackPane) n);
        }
    }

    @FXML
    void buttonClicked(Event event) {
        event.consume();
        if (Client.getState() != INIT) return;
        Button b = (Button) event.getSource();
        StackPane g = (StackPane) b.getParent();
        int i = getRow(g);
        int j = getColumn(g);
        System.out.println(i + "   " + j);
        GUIClient.setOut(i + "," + j);
    }

    @Override
    void setMove(ClientState s) {
        if (Client.verbose()) System.out.println("[DEBUG] Received new state: " + s.toString());
        StringBuilder s1 = new StringBuilder();
        if (!initialized) {
            for (Node n : gridPaneMain.getChildren()) {
                ((Button) ((StackPane) n).getChildren().get(0)).setOnAction(null);
            }
         /*   if (GUI.getPlayersNumber() == 2) {

            }*/ //TODO ??
            initialized = true;
        }
        s1.append("You have to: ");
        switch (s) {
            case MOVE -> s1.append("MOVE");
            case BUILD -> s1.append("BUILD");
            case MOVEORBUILD -> s1.append("either MOVE or BUILD"); //TODO prompt for choice (?)
            case WAIT -> s1.append("wait for your turn");
            case BUILDORPASS -> s1.append("either PASS or BUILD");
        }
        textArea1.setText(s1.toString());
    }

    @FXML
    void builderGrab(MouseEvent event) {
        grabDetected(event, builder);
    }


    @FXML
    void buildingGrab(MouseEvent event) {
        grabDetected(event, building);
    }

    private void grabDetected(MouseEvent event, DataFormat type) {
        if (Client.verbose())
            System.out.println("[DEBUG]" + new Throwable().getStackTrace()[0].getMethodName() + event.getSource() + " Entered drag");
        ImageView i = (ImageView) event.getSource();
        if (Client.verbose())
            System.out.println("[DEBUG]" + i.getParent() + " Is parent of grabbed imageview; coords of ImageView are: " + getRow(i.getParent()) + "," + getColumn(i.getParent()));
        Dragboard db = i.startDragAndDrop(TransferMode.ANY);
        Image image = (i.getImage());
        db.setDragView(image, 50, 50);
        ClipboardContent content = new ClipboardContent();
        int row = getRow(i.getParent());
        int col = getColumn(i.getParent());
        content.put(type, new Pair(row, col));
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
        if (Client.verbose())
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
        StackPane target;
        try {
            target = (StackPane) ((Button) event.getTarget()).getParent(); //TODO handle case where target is stackpane !!
        } catch (ClassCastException e) { // should only happen when event.getTarget() is an ImageView and not a button, aka when there's a builder on the cell I'm trying to move to
            if (!(event.getTarget() instanceof ImageView i1)) {
                if ((event.getTarget() instanceof StackPane)) {
                    target = (StackPane) event.getTarget();
                } else {
                    setError("Invalid move/build!");
                    event.consume();
                    return;
                }
            } else {
                if (!(Integer.parseInt(i1.getId()) / 2 == GUIClient.getGui().getPlayerIndex()))
                    target = (StackPane) i1.getParent();
                else {
                    setError("Invalid move/build!");
                    event.consume();
                    return;
                }
            }
        }
        int i = getRow(target);
        int j = getColumn(target);
        ImageView source;
        if (!isBuilding)
            source = (ImageView) ((StackPane) gridPaneMain.getChildren().get(pair.getFirst() * 5 + pair.getSecond())).getChildren().get(1);
        else source = (ImageView) (buildingPartsPane.getChildren().get(pair.getFirst()));
        switch (Client.getState()) { //TODO handle moveorbuild ,.. (?)
            case MOVE -> GUIClient.setOut(i + "," + j + "," + ((Integer.parseInt(source.getId()) % 2) + 1));
            case BUILD -> {
                if (selected == null) {
                    setError("Please click on the builder you want to use.");
                    event.consume();
                    return;
                }
                int buildingType = pair.getFirst();
                GUIClient.setOut(i + "," + j + "," + ((Integer.parseInt(selected.getId()) % 2) + 1) + "," + buildingType);
            }
        }
        event.consume();
    }

    void setText(String s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(GUIClient.getAppIcon());
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
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(GUIClient.getAppIcon());
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.setResizable(false);
        alert.showAndWait();
    }

    @Override
    void updateTable(CellView[][] table) {
        if (Client.verbose()) System.out.println("[DEBUG] Received new table.");

        selected = null;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                StackPane s = (StackPane) (gridPaneMain.getChildren()).get(i * 5 + j);
                if (s.getChildren().size() == 2) s.getChildren().remove(1);
                switch (table[i][j].getHeight()) {
                    case NONE -> addBuildingToCell(null, i, j);
                    case BASE -> addBuildingToCell(baseBuildingImage, i, j);
                    case MIDDLE -> addBuildingToCell(middleBuildingImage, i, j);
                    case TOP -> addBuildingToCell(middleBuildingImage, i, j);  //TODO add image
                    case DOME -> addBuildingToCell(middleBuildingImage, i, j); //TODO add image
                }
                switch (table[i][j].getPlayer()) {
                    case 0 -> addBuilderToCell(builderImage1, i, j, table[i][j].isFirst() ? 0 : 1);
                    case 1 -> addBuilderToCell(builderImage2, i, j, table[i][j].isFirst() ? 2 : 3);
                    case 2 -> addBuilderToCell(builderImage3, i, j, table[i][j].isFirst() ? 4 : 5);
                    case -1 -> addBuilderToCell(null, i, j, 0);
                }
            }
        }
        if (Client.getState() == BUILD) {
            addAvailablePart(baseBuildingImage, 0);
            addAvailablePart(middleBuildingImage, 1);
            addAvailablePart(baseBuildingImage, 2); //TODO cambiare immagine
        } else {
            buildingPartsPane.getChildren().clear();
        }
    }

    private void addAvailablePart(Image image, int i) {
        ImageView b = new ImageView(image);
        b.setFitHeight(155);
        b.setFitWidth(155);
        if (image == baseBuildingImage) {
            b.setUserData("baseBuilding");
        } else if (image == middleBuildingImage) {
            b.setUserData("middleBuilding");
        }//TODO aggiungere altri
        b.setScaleX(0.85);
        b.setScaleY(0.85);
        b.setOnMouseEntered(e -> b.setEffect(new Glow(1.3)));
        b.setOnMouseClicked(e -> {
            b.setScaleX(1);
            b.setScaleY(1);
        });
        b.setOnMouseExited(e -> {
            b.setEffect(null);
            b.setScaleX(0.85);
            b.setScaleY(0.85);
        });

        buildingPartsPane.add(b, 0, i);
        b.toFront();
        b.setOnDragDetected(this::buildingGrab);
    }

    private void addBuildingToCell(Image image, int row, int column) {
        if (image == null) {
            StackPane g = (StackPane) gridPaneMain.getChildren().get(row * 5 + column);
            ((Button) g.getChildren().get(0)).setGraphic(null);
        } else {
            ImageView b = new ImageView(image);
            b.setFitHeight(155);
            b.setFitWidth(155);
            b.setScaleX(1.34);
            b.setScaleY(1.34);
            b.setOpacity(0.9);
            ((Button) ((StackPane) gridPaneMain.getChildren().get(row * 5 + column)).getChildren().get(0)).setGraphic(b);
            b.toBack();
        }
    }

    private void addBuilderToCell(Image image, int row, int column, int firstBuilder) {
        if (image == null) {
            StackPane g = (StackPane) gridPaneMain.getChildren().get(row * 5 + column);
            g.getChildren().removeIf(i -> i instanceof ImageView);
        } else {
            ImageView b = new ImageView(image);
            b.setId(String.valueOf(firstBuilder));
            b.setFitHeight(155);
            b.setFitWidth(155);
                if (Client.getState() == MOVE) {
                    b.setOnDragDetected(this::builderGrab);
                    b.setOnMouseEntered(this::setHovered);
                    b.setOnMouseExited(this::unsetHovered);
                } else if (Client.getState() == BUILD) {
                    b.setOnMouseClicked(this::builderChosen);
                }
            b.setScaleX(1.6);
            b.setScaleY(1.6);
            b.setScaleZ(1.6);
            ((StackPane) gridPaneMain.getChildren().get(row * 5 + column)).getChildren().add(b);
            b.toFront();
            if (!((2 * GUIClient.getGui().getPlayerIndex()) == firstBuilder || (2 * GUIClient.getGui().getPlayerIndex() + 1) == firstBuilder)) {
                b.setOnDragDetected(null);
                b.setOnMouseEntered(null);
                b.setOnMouseExited(null);
                b.setOnMouseClicked(null);
            }
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
        b.setEffect(new Glow(2));
        b.setScaleX(1.75);
        b.setScaleY(1.75);
        b.setScaleZ(1.75);
    }

    @FXML
    void unsetHovered(Event event) {
        event.consume();
        ImageView b = (ImageView) event.getSource();
        b.setEffect(null);
        b.setScaleX(1.6);
        b.setScaleY(1.6);
        b.setScaleZ(1.6);
    }

    @FXML
    void builderChosen(Event event) {
        event.consume();
        if (selected != null) {
            selected.setEffect(null);
            selected.setScaleX(1.6);
            selected.setScaleY(1.6);
            selected.setScaleZ(1.6);
        }
        ImageView b = (ImageView) event.getSource();
        b.setEffect(new Glow(1.2));
        b.setScaleX(1.75);
        b.setScaleY(1.75);
        b.setScaleZ(1.75);
        selected = b;
    }


    void initStackPane(StackPane g) {
        g.setOnDragOver(this::dragOver);
        g.setOnDragDropped(this::dragReleased);
        Button b = new Button();
        b.setAlignment(Pos.CENTER);
        b.setOnAction(this::buttonClicked);
        g.getChildren().add(b);
        b.setVisible(true);
        b.setGraphic(new ImageView(builderImage1));
        ((ImageView) (b.getGraphic())).setFitWidth(160);
        ((ImageView) (b.getGraphic())).setFitHeight(160);
        b.getGraphic().setVisible(false);
        b.toFront();
    }

}
