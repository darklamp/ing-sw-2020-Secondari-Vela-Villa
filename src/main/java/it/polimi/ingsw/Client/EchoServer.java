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
        while(true) {
            System.out.println("33");
            String outString = "The game should be played by 2 or 3 players?";
            out.reset();
            out.writeUTF(outString);
            out.flush();
            int x = in.readInt();
            System.out.println("Il numero di giocatori è " +x);
            //if(line.equals("quit")) {
                break;
            //}
        }
        //Person p;
        /*try {
            p = (Person) in.readObject();
            //read from and write to the connection until I receive "quit"
            while(true){
                //p = (Person) in.readObject();
                String line = in.readUTF();
                if(line.equals("quit")){
                    break;
                } else {
                    String outString = "Received from " + p.getName() + ", age " + p.getAge() + " : " + line;
                    out.reset();
                    out.writeUTF(outString);
                    out.flush();
                }
            }
        } catch(ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }*/
        //close streams and socket
        System.out.println("Closing sockets");
        in.close();
        out.close();
        socket.close();
        serverSocket.close();
    }

}
