package it.polimi.ingsw.Client;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class LineClient {
    private String ip;
    private int port;
    private int NP;

    public LineClient(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public void startClient() throws IOException {

        Socket socket = new Socket(ip, port);
        System.out.println("Connection established");
        Scanner stdin = new Scanner(System.in);

        ObjectOutputStream socketOut = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream socketIn = new ObjectInputStream(socket.getInputStream());

        //socketOut.reset();
        //socketOut.writeObject(p);
        //socketOut.flush();

        try{
            while (true){


                String socketLine = socketIn.readUTF();
                System.out.println(socketLine);
                Scanner input = new Scanner (System.in);
                int NP = input.nextInt();
                System.out.println("Hai scritto " + NP);
                socketOut.reset();
                socketOut.writeInt(NP);
                socketOut.flush();
                System.out.println("mandato");

            }
        }
        catch (IOException e) {
            System.out.println("Connection closed");
        } finally {
            stdin.close();
            socketIn.close();
            socketOut.close();
            socket.close();
        }
    }

}
