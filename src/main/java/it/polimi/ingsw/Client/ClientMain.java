package it.polimi.ingsw.Client;

import it.polimi.ingsw.Client.CLI.CLI;
import it.polimi.ingsw.Client.GUI.GUI;
import it.polimi.ingsw.Client.GUI.GUIClient;
import it.polimi.ingsw.Utility.Color;
import it.polimi.ingsw.Utility.Utils;

import java.io.IOException;

import static java.lang.Thread.sleep;

public class ClientMain {

    public static void main(String[] args){
        int port = 1337;
        boolean useGui = true;
        boolean debug = false;
        String ip = null;
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
                try {
                    ip = a.split("=")[1];
                    if (!ip.contains("'")) {
                        StringBuilder s = new StringBuilder();
                        s.append("'");
                        s.append(ip);
                        s.append("'");
                        ip = s.toString();
                    }
                    if (Utils.isValidIP(ip)) throw new Exception();
                } catch (Exception e) {
                    System.out.println("Invalid IP" + ip + "!");
                    System.exit(0);
                }
            } else if (a.toLowerCase().contains("v")) {
                debug = true;
            }
        }
        Client client = new Client(port, debug);
        Ui ui;
        //Scanner stdin = new Scanner(System.in);
        System.out.println(Color.ANSI_BLUE);

        String s = """
                 $$$$$$\\   $$$$$$\\  $$\\   $$\\ $$$$$$$$\\  $$$$$$\\  $$$$$$$\\  $$$$$$\\ $$\\   $$\\ $$$$$$\\\s
                $$  __$$\\ $$  __$$\\ $$$\\  $$ |\\__$$  __|$$  __$$\\ $$  __$$\\ \\_$$  _|$$$\\  $$ |\\_$$  _|
                $$ /  \\__|$$ /  $$ |$$$$\\ $$ |   $$ |   $$ /  $$ |$$ |  $$ |  $$ |  $$$$\\ $$ |  $$ | \s
                \\$$$$$$\\  $$$$$$$$ |$$ $$\\$$ |   $$ |   $$ |  $$ |$$$$$$$  |  $$ |  $$ $$\\$$ |  $$ | \s
                 \\____$$\\ $$  __$$ |$$ \\$$$$ |   $$ |   $$ |  $$ |$$  __$$<   $$ |  $$ \\$$$$ |  $$ | \s
                $$\\   $$ |$$ |  $$ |$$ |\\$$$ |   $$ |   $$ |  $$ |$$ |  $$ |  $$ |  $$ |\\$$$ |  $$ | \s
                \\$$$$$$  |$$ |  $$ |$$ | \\$$ |   $$ |    $$$$$$  |$$ |  $$ |$$$$$$\\ $$ | \\$$ |$$$$$$\\\s
                 \\______/ \\__|  \\__|\\__|  \\__|   \\__|    \\______/ \\__|  \\__|\\______|\\__|  \\__|\\______|
                """;

        //System.out.println("Would you like to use a fancy GUI? (y/N)");
        // String choice = stdin.nextLine().toUpperCase();
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
            System.out.println(Color.ANSI_BLUE + s + Color.RESET);
            CLI cli = new CLI();
            Client.setUi(cli);
            System.out.print("Starting CLI..\n");
            ui = cli;
        }
        try {
            ui.waitForIP(client);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
