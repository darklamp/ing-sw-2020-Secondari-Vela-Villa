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

package it.polimi.ingsw.Client;

import it.polimi.ingsw.Client.CLI.CLI;
import it.polimi.ingsw.Client.GUI.GUI;
import it.polimi.ingsw.Client.GUI.GUIClient;
import it.polimi.ingsw.Utility.Color;

import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static java.lang.Thread.sleep;

/**
 * Launch the client
 */
public class ClientMain {

    /**
     * Simple flag to indicate if the client has been invoked with the -ip flag
     */
    private static boolean ipFlag = false;

    public static boolean validIP() {
        return ipFlag;
    }

    public static void setValidIP() {
        ipFlag = true;
    }

    public static void main(String[] args) {
        int port = 1337;
        boolean useGui = true;
        try {
            Class.forName("javafx.application.Application");
        } catch (ClassNotFoundException e) {
            useGui = false;
        }
        boolean debug = false;
        InetAddress ip = null;
        try {
            ip = InetAddress.getByName("localhost");
        } catch (UnknownHostException ignored) {
        }
        String ipString;
        for (String a : args) {
            if (a.toLowerCase().contains("cli")) {
                useGui = false;
            } else if (a.toLowerCase().contains("port")) {
                try {
                    port = Integer.parseInt(a.split("=")[1]);
                } catch (Exception e) {
                    System.out.println("Invalid port!");
                    System.exit(0);
                }
            } else if (a.toLowerCase().contains("ip")) {
                ipFlag = true;
                ipString = a.split("=")[1];
                try {
                    ip = InetAddress.getByName(ipString);
                } catch (Exception e) {
                    System.out.println("Invalid IP" + ip + "!");
                    System.exit(0);
                }
            } else if (a.toLowerCase().contains("v")) {
                debug = true;
            }
        }
        Client client = new Client(ip, port, debug);
        Ui ui;
        System.out.println(Color.ANSI_BLUE);

        String s = "\n $$$$$$\\   $$$$$$\\  $$\\   $$\\ $$$$$$$$\\  $$$$$$\\  $$$$$$$\\  $$$$$$\\ $$\\   $$\\ $$$$$$\\\n$$  __$$\\ $$  __$$\\ $$$\\  $$ |\\__$$  __|$$  __$$\\ $$  __$$\\ \\_$$  _|$$$\\  $$ |\\_$$  _|\n$$ /  \\__|$$ /  $$ |$$$$\\ $$ |   $$ |   $$ /  $$ |$$ |  $$ |  $$ |  $$$$\\ $$ |  $$ | \n\\$$$$$$\\  $$$$$$$$ |$$ $$\\$$ |   $$ |   $$ |  $$ |$$$$$$$  |  $$ |  $$ $$\\$$ |  $$ | \n \\____$$\\ $$  __$$ |$$ \\$$$$ |   $$ |   $$ |  $$ |$$  __$$<   $$ |  $$ \\$$$$ |  $$ | \n$$\\   $$ |$$ |  $$ |$$ |\\$$$ |   $$ |   $$ |  $$ |$$ |  $$ |  $$ |  $$ |\\$$$ |  $$ | \n\\$$$$$$  |$$ |  $$ |$$ | \\$$ |   $$ |    $$$$$$  |$$ |  $$ |$$$$$$\\ $$ | \\$$ |$$$$$$\\\n \\______/ \\__|  \\__|\\__|  \\__|   \\__|    \\______/ \\__|  \\__|\\______|\\__|  \\__|\\______|\n";

        if (useGui) {
            GUI gui = new GUI();
            new Thread(gui).start();
            GUIClient.setGUI(gui);
            System.out.println("Starting GUI..");
            StringBuilder s1 = new StringBuilder();
            while (!gui.isReady()) {
                try {
                    sleep(20);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                s1.append("█");
                System.out.print(s1.toString() + "\r");
            }
            System.out.println(Color.ANSI_GREEN + s1.append("\n").toString() + "GUI Ready!\n" + Color.RESET);
            Client.setUi(gui);
            ui = gui;
        }
        else {
            if (!GraphicsEnvironment.isHeadless()) {
                SplashScreen splashScreen = SplashScreen.getSplashScreen();
                if (splashScreen != null) {
                    splashScreen.close();
                }
            }
            System.out.println(Color.ANSI_BLUE + s + Color.RESET);
            CLI cli = new CLI();
            Client.setUi(cli);
            System.out.print("Starting CLI..\n");
            ui = cli;
        }
        ui.waitForIP(client);
    }

}
