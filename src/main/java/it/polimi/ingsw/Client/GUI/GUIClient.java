package it.polimi.ingsw.Client.GUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


public class GUIClient extends Application {

    static Image getAppIcon() {
        return appIcon;
    }

    private static Image appIcon;

    static Font getDefaultFont() {
        return defaultFont;
    }

    /**
     * Getter for specific size of font
     * @param size size of font
     * @return font in specified size
     */
    static Font getDefaultFont(double size) {
        return size > 0 ? Font.font(defaultFont.getName(), size) : defaultFont;
    }

    private static Font defaultFont;

    static GUI getGui() {
        return gui;
    }

    private static GUI gui = null;

    static WindowController getController() {
        return controller;
    }

    static void setController(WindowController controller) {
        GUIClient.controller = controller;
    }

    private static WindowController controller;

    static Stage getStage() {
        return stage;
    }

    static void setStage(Stage stage) {
        GUIClient.stage = stage;
    }

    private static Stage stage;
    private static String news;
    private static PropertyChangeSupport support;

    /**
     * Listener helper object
     **/
    static void setOut(String s) {
        support.firePropertyChange("", news, s);
        news = s;
    }

    /**
     * @param pcl PropertyChangeListener to be added
     */
    static void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    /**
     * Default method for main JavaFx class; loads stage, LoginController's scene, and a bunch of useful elements like fonts.
     *
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        support = new PropertyChangeSupport(this);
        Parent root = loader.load();
        controller = loader.getController();
        primaryStage.setTitle("Santorini Client");
        primaryStage.setScene(new Scene(root, 400, 250));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
        stage = primaryStage;
        WindowController.setCursor(new ImageCursor(new Image("/images/cursor.png")));
        stage.getScene().setCursor(WindowController.getCursor());
        appIcon = new Image("/images/appIcon.png");
        stage.getIcons().add(appIcon);
        Font.loadFont(getClass().getResourceAsStream("/fonts/DalekPinpoint.ttf"), 35);
        defaultFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Roman_SD.ttf"), 35);
        WindowController.setErrorIcon(new ImageView(new Image("/images/errorIcon.png")));
        gui.setReady(true);
        stage.setAlwaysOnTop(false);
        stage.setOnCloseRequest(we -> {
            System.out.println("Exiting...");
            stage.close();
            Platform.exit();
            System.exit(0);
        });
    }


    public static void setGUI(GUI gui){
        if (GUIClient.gui == null)         GUIClient.gui = gui;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
