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
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Dimension2D;
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
     * @param primaryStage default stage
     * @throws Exception if fxml loading doesn't go well
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        //load login graphics
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        support = new PropertyChangeSupport(this);
        Parent root = loader.load();
        primaryStage.setFullScreenExitHint("Please use F11 to toggle fullscreen on/off.");
        controller = loader.getController();
        primaryStage.setTitle("Santorini Client");
        primaryStage.setScene(new Scene(root, 400, 250));
        primaryStage.initStyle(StageStyle.UNDECORATED);

        primaryStage.show();
        stage = primaryStage;

        // get system preferred cursor size and set cursor property
        Dimension2D dimension2D = ImageCursor.getBestSize(64, 64);
        if (Client.verbose())
            System.out.println("[DEBUG] Setting cursor size: " + dimension2D.getHeight() + "x" + dimension2D.getWidth());
        WindowController.setCursor(new ImageCursor(new Image("/images/cursor.png"), dimension2D.getHeight(), dimension2D.getWidth()));
        stage.getScene().setCursor(WindowController.getCursor());

        // set app icon
        appIcon = new Image("/images/appIcon.png");
        stage.getIcons().add(appIcon);

        // add fonts
        Font.loadFont(getClass().getResourceAsStream("/fonts/DalekPinpoint.ttf"), 35);
        defaultFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Roman_SD.ttf"), 35);

        // add error icon
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
