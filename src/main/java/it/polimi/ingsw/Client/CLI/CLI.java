package it.polimi.ingsw.Client.CLI;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Client.Ui;
import it.polimi.ingsw.Utility.Utils;

import java.io.IOException;
import java.util.Scanner;

public class CLI implements Ui {
    @Override
    public void process(String input) {
        System.out.println(input);
    }

    @Override
    public void waitForIP(Client client) throws IOException {
        Scanner stdin  = new Scanner(System.in);
        System.out.println("Insert IP address: ");
        String s = stdin.nextLine();
        while (!Utils.isValidIP(s)){
            System.out.println("Insert IP address: ");
            s = stdin.nextLine();
        }
        client.run(s);
    }
}
