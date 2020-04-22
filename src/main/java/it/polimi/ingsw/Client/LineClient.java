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
    private String ip,id,s;
    private int port,x;
    Scanner input;

    public LineClient(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public void startClient() throws IOException {

        Socket socket = new Socket(ip, port);
        System.out.println("Connection established");
        Scanner stdin = new Scanner(System.in);
        System.out.println("29");
        ObjectOutputStream socketOut = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream socketIn = new ObjectInputStream(socket.getInputStream());
        System.out.println("30");
        int status = socketIn.readInt();
        System.out.println("Server is in status " +status);
        try{
            while (true) {
                if (status == 1) {
                    s = socketIn.readUTF();
                    System.out.println(s);
                    input = new Scanner(System.in);
                    id = input.nextLine();
                    socketOut.reset();
                    socketOut.writeUTF(id);
                    socketOut.flush();
                    s = socketIn.readUTF();
                    System.out.println(s);
                    input = new Scanner(System.in);
                    x = input.nextInt();
                    while (x < 2 || x > 3) {
                        System.out.println("Invalid value, you have to choose between 2 and 3. Try again");
                        x = input.nextInt();
                    }
                    socketOut.reset();
                    socketOut.writeInt(x);
                    socketOut.flush();
                }
                if ( status == 2){
                    s = socketIn.readUTF();
                    System.out.println(s);
                    input = new Scanner(System.in);
                    id = input.nextLine();
                    socketOut.reset();
                    socketOut.writeUTF(id);
                    socketOut.flush();
                    //s = socketIn.readUTF();
                    //System.out.println(s);
                }
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
