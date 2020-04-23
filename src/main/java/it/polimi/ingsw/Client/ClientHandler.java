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
    private int x,k,z;
    private String s, id1, id2, id3;
    private int status=1; //stato 1: aspetto che qualcuno crei una partita. Stato 2: aspetto giocatori per riempirla. Stato 3: pronto

    public ClientHandler(Socket socket,int z) {
        this.socket = socket;
        this.z = z;
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

            System.out.println("Received client connection");
            // open input and output streams to read and write
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            status=getStatus();
            out.reset();
            out.writeInt(z);
            out.flush();
            while (true) {
                if(z==1) {
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
                }
                else{
                    s = "Hi, You're the second player!What's your id?";
                    out.reset();
                    out.writeUTF(s);
                    out.flush();
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
