package it.polimi.ingsw.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.GregorianCalendar;
import java.util.Scanner;

public class EchoServer {

    private int port;
    private ServerSocket serverSocket;
    private int x;
    private String s,id1,id2,id3;
    private int status=1; //stato 1: aspetto che qualcuno crei una partita. Stato 2: aspetto giocatori per riempirla. Stato 3: pronto

    public EchoServer(int port){
        this.port = port;
    }


    public void startServer() throws IOException {
        //open TCP port
        serverSocket = new ServerSocket(port);
        System.out.println("Server socket ready on port: " + port);
        //wait for connection
        Socket socket = serverSocket.accept();
        System.out.println("Received client connection");
        // open input and output streams to read and write
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        out.reset();
        out.writeInt(status);
        out.flush();
        while(true) {
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
                status ++;
                while( x > 1) {
                    socket = serverSocket.accept();
                    System.out.println("Received client connection");
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
                status++;
                if( status == 3){
                    break;
                }
        }
        //close streams and socket
        System.out.println("Closing sockets");
        in.close();
        out.close();
        socket.close();
        serverSocket.close();
    }

}
