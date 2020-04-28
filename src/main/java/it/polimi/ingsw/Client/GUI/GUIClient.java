package it.polimi.ingsw.Client.GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class GUIClient extends Application {


    private static GUI gui = null;

    static GUIController getController() {
        return controller;
    }

    private static GUIController controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        primaryStage.setTitle("Santorini Client");
        primaryStage.setScene(new Scene(root,400,150));
        primaryStage.show();
        gui.setReady(true);
    }



    public static void setGUI(GUI gui){
        if (GUIClient.gui == null)         GUIClient.gui = gui;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
