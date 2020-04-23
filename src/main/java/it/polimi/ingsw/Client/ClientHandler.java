package it.polimi.ingsw.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.GregorianCalendar;
import java.util.Scanner;

public class ClientHandler implements Runnable {

    private int port;
    private ServerSocket serverSocket;
    private Socket socket;
    private int x,k;
    private String s, id1, id2, id3;
    private int status=1; //stato 1: aspetto che qualcuno crei una partita. Stato 2: aspetto giocatori per riempirla. Stato 3: pronto

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }
    public int getStatus()
    {
        return this.status;
    }
    public void setStatus(int k)
    {
        this.status = k;
    }
    public void run() {
        try {

            //open TCP port
            //serverSocket = new ServerSocket(port);
            //System.out.println("Server socket ready on port: " + port);
            //wait for connection
            //Socket socket = serverSocket.accept();
            System.out.println("Received client connection");
            // open input and output streams to read and write
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            status=getStatus();
            out.reset();
            out.writeInt(status);
            out.flush();
            while (true) {
                s = "Hi, you're the first in the lobby. What's your id?";
                out.reset();
                out.writeUTF(s);
                out.flush();
                id1 = in.readUTF();
                System.out.println(id1);
                s = "The game should be played by 2 or 3 players?";
                out.reset();
                out.writeUTF(s);
                out.flush();
                int x = in.readInt();
                System.out.println("Player number is " + x);
                setStatus(2);
                while (x > 1) {
                    socket = serverSocket.accept();
                    System.out.println("Received client connection");
                    status=getStatus();
                    out.reset();
                    out.writeInt(status);
                    out.flush();
                    s = "Hi, You're the second player!What's your id?";
                    out.reset();
                    out.writeUTF(s);
                    out.flush();
                    System.out.println("aa");
                    id2 = in.readUTF();
                    System.out.println(id2);
                    x--;
                }
                setStatus(3);
                status=getStatus();
                if (status == 3) {
                    break;
                }
            }
            //close streams and socket
            System.out.println("Closing sockets");
            in.close();
            out.close();
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
