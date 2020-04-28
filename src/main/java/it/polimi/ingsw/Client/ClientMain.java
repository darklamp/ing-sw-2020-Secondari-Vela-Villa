package it.polimi.ingsw.Client;

import it.polimi.ingsw.Client.CLI.CLI;
import it.polimi.ingsw.Client.GUI.GUI;
import it.polimi.ingsw.Client.GUI.GUIClient;
import it.polimi.ingsw.Utility.Color;


import java.io.IOException;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class ClientMain {

    public static void main(String[] args){
        Client client = new Client(1337);
        Ui ui;
        Scanner stdin = new Scanner(System.in);
        System.out.println(Color.ANSI_BLUE);
        String s = """
         ________  ________  ________   _________  ________  ________  ___  ________   ___    \s
        |\\   ____\\|\\   __  \\|\\   ___  \\|\\___   ___\\\\   __  \\|\\   __  \\|\\  \\|\\   ___  \\|\\  \\   \s
        \\ \\  \\___|\\ \\  \\|\\  \\ \\  \\\\ \\  \\|___ \\  \\_\\ \\  \\|\\  \\ \\  \\|\\  \\ \\  \\ \\  \\\\ \\  \\ \\  \\  \s
         \\ \\_____  \\ \\   __  \\ \\  \\\\ \\  \\   \\ \\  \\ \\ \\  \\\\\\  \\ \\   _  _\\ \\  \\ \\  \\\\ \\  \\ \\  \\ \s
          \\|____|\\  \\ \\  \\ \\  \\ \\  \\\\ \\  \\   \\ \\  \\ \\ \\  \\\\\\  \\ \\  \\\\  \\\\ \\  \\ \\  \\\\ \\  \\ \\  \\\s
            ____\\_\\  \\ \\__\\ \\__\\ \\__\\\\ \\__\\   \\ \\__\\ \\ \\_______\\ \\__\\\\ _\\\\ \\__\\ \\__\\\\ \\__\\ \\__\\
           |\\_________\\|__|\\|__|\\|__| \\|__|    \\|__|  \\|_______|\\|__|\\|__|\\|__|\\|__| \\|__|\\|__|
           \\|_________|                                                                       \s
        
        """;

        System.out.println(Color.ANSI_BLUE + s + Color.RESET);

        System.out.println("Would you like to use a fancy GUI? (y/N)");
        String choice = stdin.nextLine().toUpperCase();
        if (choice.equals("Y") || choice.equals("YES") || choice.equals("SI") || choice.equals("OUI")){
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
            CLI cli = new CLI();
            Client.setUi(cli);
            System.out.print("Starting CLI..\n");
            ui = cli;
        }
        try {
            ui.waitForIP(client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
