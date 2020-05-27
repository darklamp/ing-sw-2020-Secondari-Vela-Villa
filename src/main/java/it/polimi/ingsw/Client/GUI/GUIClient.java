package it.polimi.ingsw.Client.GUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


public class GUIClient extends Application {

    static Image getAppIcon() {
        return appIcon;
    }

    private static Image appIcon;

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

    public static Stage getStage() {
        return stage;
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

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
        support = new PropertyChangeSupport(this);
        Parent root = loader.load();
        controller = loader.getController();
        primaryStage.setTitle("Santorini Client");
        primaryStage.setScene(new Scene(root, 400, 250));
        primaryStage.show();
        stage = primaryStage;
        appIcon = new Image("/images/appIcon.png");
        stage.getIcons().add(appIcon);
        gui.setReady(true);
        stage.setAlwaysOnTop(false);
        stage.setOnCloseRequest(we -> {
            System.out.println("Exiting...");
            stage.close();
            Platform.exit();
            System.exit(0);
        });
        stage.setFullScreenExitHint("Mmmmm fullscreen");
    }


    public static void setGUI(GUI gui){
        if (GUIClient.gui == null)         GUIClient.gui = gui;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
