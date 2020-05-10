package it.polimi.ingsw.Client.GUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class GUIClient extends Application {


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

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        primaryStage.setTitle("Santorini Client");
        primaryStage.setScene(new Scene(root, 400, 150));
        primaryStage.show();
        stage = primaryStage;
        gui.setReady(true);
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
