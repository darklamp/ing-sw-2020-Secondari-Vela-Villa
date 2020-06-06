package it.polimi.ingsw.Client.GUI;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Utility.Pair;
import it.polimi.ingsw.View.CellView;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static it.polimi.ingsw.Client.ClientState.*;

public class MainWindowController extends WindowController implements Initializable {

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
    @FXML
    StackPane parts0, parts1, parts2, parts3;
    @FXML
    ProgressBar timerBar;
    @FXML
    ImageView godImage;

    private static Timeline timeline;
    private static CellView[][] lastTable;
    private static final SimpleStringProperty TEXT_COLOR = new SimpleStringProperty("papayawhip");

    private static final DataFormat builder = new DataFormat("builder");
    private static final DataFormat building = new DataFormat("building");
    private static boolean newTurn = true;

    private static final double MAX_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    private static final double MAX_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
    private static final double SCREEN_RATIO = 16.0 / 9.0;

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
    private final static Image topBuildingImage = new Image("/images/buildingTop.png");
    private final static Image domeBuildingImage = new Image("/images/buildingDome.png");


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
        b.setStyle("-fx-background-color: orange;");
        b.setOpacity(0.8);
        StackPane g = (StackPane) b.getParent();
        int i = getRow(g);
        int j = getColumn(g);
        System.out.println(i + "   " + j);
        GUIClient.setOut(i + "," + j);
    }

    private void clearAvailableParts() {
        if (parts0.getChildren().size() != 0) parts0.getChildren().clear();
        if (parts1.getChildren().size() != 0) parts1.getChildren().clear();
        if (parts2.getChildren().size() != 0) parts2.getChildren().clear();
        if (parts3.getChildren().size() != 0) parts3.getChildren().clear();
    }

    private void setTimerBar() {
        newTurn = false;
        timerBar.setVisible(true);
        timerBar.setStyle("-fx-background-color: transparent;");
        timeline = new Timeline(60,
                new KeyFrame(Duration.ZERO, new KeyValue(timerBar.progressProperty(), 0)),
                new KeyFrame(Duration.seconds(90), e -> {
                    textArea1.setText("\nHurry up! Time's about to finish.");
                    TEXT_COLOR.set("red");
                    timerBar.setStyle("-fx-accent: red;");
                }, new KeyValue(timerBar.progressProperty(), 0.75)),
                new KeyFrame(Duration.seconds(120), e -> {
                    textArea1.setText("Time's up!");
                    TEXT_COLOR.set("red");
                    timerBar.setStyle("-fx-accent: red;");
                }, new KeyValue(timerBar.progressProperty(), 1))
        );
        timeline.setCycleCount(1);
        timeline.play();
        timeline.setOnFinished(e -> {
            timeline.stop();
        });
    }

    @Override
    synchronized void setMove(ClientState s) {
        if (Client.verbose()) System.out.println("[DEBUG] Received new state: " + s.toString());
        clearAvailableParts();
        StringBuilder s1 = new StringBuilder();
        if (!initialized) {
            for (Node n : gridPaneMain.getChildren()) {
                ((Button) ((StackPane) n).getChildren().get(0)).setOnAction(null);
                ((StackPane) n).getChildren().get(0).setStyle(null);
                ((StackPane) n).getChildren().get(0).setOpacity(1);
            }
            StringProperty stringProperty = new SimpleStringProperty();
            GUIClient.getStage().getScene().heightProperty().addListener(((observableValue, oldV, newV) -> stringProperty.setValue("-fx-font-size: " + (int) (GUIClient.getStage().getScene().heightProperty().get() / MAX_HEIGHT * 30) + "px; -fx-text-fill: " + TEXT_COLOR.get() + ";")));
            TEXT_COLOR.addListener((observableValue, oldV, newV) -> stringProperty.setValue("-fx-font-size: " + (int) (GUIClient.getStage().getScene().heightProperty().get() / MAX_HEIGHT * 30) + "px; -fx-text-fill: " + TEXT_COLOR.get() + ";"));
            textArea1.styleProperty().bind(stringProperty);
            GUIClient.getStage().getScene().setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.F11) {
                    if (GUIClient.getStage().isFullScreen()) {
                        GUIClient.getStage().minWidthProperty().bind(GUIClient.getStage().getScene().heightProperty().multiply(SCREEN_RATIO));
                        GUIClient.getStage().minHeightProperty().bind(GUIClient.getStage().getScene().widthProperty().divide(SCREEN_RATIO));

                        GUIClient.getStage().setFullScreen(false);
                    } else {
                        GUIClient.getStage().minWidthProperty().unbind();
                        GUIClient.getStage().minHeightProperty().unbind();
                        GUIClient.getStage().setFullScreen(true);
                    }

                }
            });
            initialized = true;
            godImage.setImage(new Image("/images/GodCards/" + (Client.getGod() + 1) + ".png"));
            godImage.fitHeightProperty().bind(GUIClient.getStage().heightProperty().divide(MAX_HEIGHT).multiply(godImage.getImage().getHeight() / (3.75 * 1080 / MAX_HEIGHT)));
            godImage.fitWidthProperty().bind(GUIClient.getStage().widthProperty().divide(MAX_WIDTH).multiply(godImage.getImage().getWidth() / (3.75 * 1920 / MAX_WIDTH)));
        }
        s1.append("Current turn: ");
        TEXT_COLOR.set("papayawhip");
        if (newTurn) setTimerBar();
        switch (s) {
            case MOVE -> {
                s1.append("yours! You have to MOVE");
            }
            case BUILD -> {
                s1.append("yours! You have to BUILD");
                addAvailablePart(baseBuildingImage, 0);
                addAvailablePart(middleBuildingImage, 1);
                addAvailablePart(topBuildingImage, 2);
                addAvailablePart(domeBuildingImage, 3);
            }
            case MOVEORBUILD -> {
                boolean move = setStateChoiceDialog("MOVE", "BUILD");
                if (move) {
                    Client.setState(MOVE);
                    GUIClient.getGui().processTurnChange(MOVE);
                } else {
                    Client.setState(BUILD);
                    GUIClient.getGui().processTurnChange(BUILD);
                }
                updateTable(lastTable);
            }
            case WAIT -> {
                s1.append(Client.getCurrentPlayer());
                timeline.stop();
                timerBar.setVisible(false);
                newTurn = true;
            }
            case BUILDORPASS -> {
                boolean build = setStateChoiceDialog("BUILD", "PASS");
                if (build) {
                    Client.setState(BUILD);
                    GUIClient.getGui().processTurnChange(BUILD);
                } else {
                    GUIClient.setOut("PASS");
                    setMove(WAIT);
                    /// Client.setState(WAIT);
                    newTurn = false;
                }
                updateTable(lastTable);
            }
            case WIN -> {
                timerBar.setVisible(false);
                setChoiceDialog("WINNER", null, "Congrats! Looks like you just won.", "Rejoice", "Rejoice", WIN);
                Platform.exit();
                System.exit(0);
            }
            case LOSE -> {
                timerBar.setVisible(false);
                setChoiceDialog("LOSER", null, "Looks like you lost.", "Shame", "Shame", LOSE);
                Platform.exit();
                System.exit(0);
            }
            case INIT -> {
            }
        }
        textArea1.setText(s1.toString());
    }

    private boolean setStateChoiceDialog(String opt1, String opt2) {
        return setChoiceDialog("Turn choice", null, "Please choose what you want to do now: ", opt1, opt2, null);
    }

    private boolean setChoiceDialog(String title, String header, String text, String opt1, String opt2, ClientState c) {
        List<String> choices = new ArrayList<>();
        choices.add(opt1);
        choices.add(opt2);
        if (c != null) for (int i = 0; i < 10; i++) choices.add(opt1);
        ChoiceDialog<String> dialog = new ChoiceDialog<>(opt1, choices);
        if (c != null) {
            if (c == WIN) dialog.setGraphic(new ImageView(new Image("/images/confetti.gif")));
            else if (c == LOSE) dialog.setGraphic(new ImageView(new Image("/images/loser.gif")));
        }
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(text);
        Optional<String> result = dialog.showAndWait();
        while (true) {
            if (result.isPresent()) {
                if (!result.get().equals(opt1) && !result.get().equals(opt2)) {
                    result = dialog.showAndWait();
                } else {
                    break;
                }
            } else {
                result = dialog.showAndWait();
            }
        }
        return result.get().equals(opt1);
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
        Image image;
        if (i.getImage() == builderImage1) image = builderImage2;
        else if (i.getImage() == builderImage2) image = builderImage1;
        else image = i.getImage();
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
            target = (StackPane) ((Button) event.getTarget()).getParent();
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
                if (!(Integer.parseInt(i1.getId()) / 2 == GUI.getPlayerIndex()))
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
        else {
            try {
                source = (ImageView) (buildingPartsPane.getChildren().get(pair.getFirst()));
            } catch (ClassCastException e) {
                source = (ImageView) ((StackPane) (buildingPartsPane.getChildren().get(pair.getFirst()))).getChildren().get(0);
            }
        }
        switch (Client.getState()) {
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
    synchronized void updateTable(CellView[][] table) {

        lastTable = table;

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
                    case TOP -> addBuildingToCell(topBuildingImage, i, j);
                    case DOME -> addBuildingToCell(domeBuildingImage, i, j);
                }
                switch (table[i][j].getPlayer()) {
                    case 0 -> addBuilderToCell(builderImage1, i, j, table[i][j].isFirst() ? 0 : 1);
                    case 1 -> addBuilderToCell(builderImage2, i, j, table[i][j].isFirst() ? 2 : 3);
                    case 2 -> addBuilderToCell(builderImage3, i, j, table[i][j].isFirst() ? 4 : 5);
                    case -1 -> addBuilderToCell(null, i, j, 0);
                }
            }
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
        } else if (image == topBuildingImage) {
            b.setUserData("topBuilding");
        } else {
            b.setUserData("domeBuilding");
        }
        b.setOnMouseEntered(e -> b.setEffect(new Glow(1.3)));
        b.setScaleX(1.1);
        b.setScaleY(1.1);
        b.setOnMouseClicked(e -> {
            b.setScaleX(1.2);
            b.setScaleY(1.2);
        });
        b.setOnMouseExited(e -> {
            b.setEffect(null);
            b.setScaleX(1.1);
            b.setScaleY(1.1);
        });

        StackPane s = switch (i) {
            case 0 -> parts0;
            case 1 -> parts1;
            case 2 -> parts2;
            case 3 -> parts3;
            default -> throw new IllegalStateException("Unexpected value: " + i);
        };
        if (Client.verbose()) System.out.println("[DEBUG] " + s.getId());
        s.getChildren().add(b);
        b.toFront();
        b.setOnDragDetected(this::buildingGrab);
    }

    private void addBuildingToCell(Image image, int row, int column) {
        if (image == null) {
            StackPane g = (StackPane) gridPaneMain.getChildren().get(row * 5 + column);
            ((Button) g.getChildren().get(0)).setGraphic(null);
        } else {
            ImageView b = new ImageView(image);
            b.fitHeightProperty().bind(gridPaneMain.heightProperty().divide(5).add(55));
            b.fitWidthProperty().bind(gridPaneMain.widthProperty().divide(5).add(55));
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
            b.fitHeightProperty().bind(gridPaneMain.heightProperty().divide(5).add(55));
            b.fitWidthProperty().bind(gridPaneMain.widthProperty().divide(5).add(55));
            ((StackPane) gridPaneMain.getChildren().get(row * 5 + column)).getChildren().add(b);
            b.toFront();
            if (!((2 * GUI.getPlayerIndex()) == firstBuilder || (2 * GUI.getPlayerIndex() + 1) == firstBuilder)) {
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
        b.setSmooth(true);
        b.setScaleX(1.15);
        b.setScaleY(1.15);
        b.setScaleZ(1.15);
    }

    @FXML
    void unsetHovered(Event event) {
        event.consume();
        ImageView b = (ImageView) event.getSource();
        b.setEffect(null);
        b.setSmooth(true);
        b.setScaleX(1);
        b.setScaleY(1);
        b.setScaleZ(1);
    }

    @FXML
    void builderChosen(Event event) {
        event.consume();
        if (selected != null) {
            selected.setEffect(null);
            selected.setScaleX(1);
            selected.setScaleY(1);
            selected.setScaleZ(1);
        }
        ImageView b = (ImageView) event.getSource();
        b.setEffect(new Glow(1.2));
        b.setScaleX(1.15);
        b.setScaleY(1.15);
        b.setScaleZ(1);
        selected = b;
    }


    private void initStackPane(StackPane g) {
        g.setMinSize(Region.USE_PREF_SIZE, Region.USE_COMPUTED_SIZE);
        g.setMaxSize(Region.USE_PREF_SIZE, Region.USE_COMPUTED_SIZE);
        g.prefHeightProperty().bind(gridPaneMain.heightProperty().divide(5));
        g.prefWidthProperty().bind(gridPaneMain.widthProperty().divide(5));
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
