//NON DA FARE
package it.polimi.ingsw.Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiEchoServer {
    private int port;

    public MultiEchoServer(int port){
        this.port = port;
    }

    public void startServer() throws IOException{
        //It creates threads when necessary, otherwise it re-uses existing one when possible
        ExecutorService executor = Executors.newCachedThreadPool();
        ServerSocket serverSocket;
        try{
            serverSocket = new ServerSocket(port);
        }catch (IOException e){
            System.err.println(e.getMessage()); //port not available
            return;
        }
        System.out.println("Server ready");
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("Conessione primo ok"); //crea client con parametro, se è il primo tipo gli passo 1 come parametro e gli faccio fare qualcosa,altrimenti no!!
                executor.submit(new ClientHandler(socket, 1));
                socket = serverSocket.accept();
                System.out.println("Conessione secondo ok");
                executor.submit(new ClientHandler(socket, 2));
            } catch (IOException e) {
                break; //In case the serverSocket gets closed
            }
        }
        executor.shutdown();
        serverSocket.close();
    }
}