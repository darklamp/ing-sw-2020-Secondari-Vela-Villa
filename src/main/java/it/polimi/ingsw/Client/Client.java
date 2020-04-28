package it.polimi.ingsw.Client;

import it.polimi.ingsw.Utility.Color;
import it.polimi.ingsw.View.CellView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static it.polimi.ingsw.Model.BuildingType.NONE;

public class Client implements Runnable {
    private static String ip;
    private static int port;
    private static Ui ui;

    public Client(int port){
        Client.port = port;
    }

    static void setUi(Ui ui){
        Client.ui = ui;
    }

    public Thread asyncReadFromSocket(final ObjectInputStream socketIn){
        Thread t = new Thread(() -> {
            try {
                while (isActive()) {
                    Object inputObject = socketIn.readObject();
                    if(inputObject instanceof String){
                        ui.process((String) inputObject);
                    } else if (inputObject instanceof CellView[][]){
                        ui.showTable((CellView[][])inputObject);
                    } else {
                        throw new IllegalArgumentException();
                    }
                }
            } catch (Exception e){
                setActive(false);
            }
        });
        t.start();
        return t;
    }


    private boolean active = true;

    public synchronized boolean isActive(){
        return active;
    }

    public synchronized void setActive(boolean active){
        this.active = active;
    }

    public Thread asyncWriteToSocket(final Scanner stdin, final PrintWriter socketOut){
        Thread t = new Thread(() -> {
            try {
                while (isActive()) {
                    String inputLine = stdin.nextLine();
                    socketOut.println(inputLine);
                    socketOut.flush();
                }
            }catch(Exception e){
                setActive(false);
            }
        });
        t.start();
        return t;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(ip, port);
            ui.process("Connection established");
            Scanner stdin = new Scanner(System.in);
            PrintWriter socketOut = new PrintWriter(socket.getOutputStream());
            ObjectInputStream socketIn = new ObjectInputStream(socket.getInputStream());
            try{
                Thread t0 = asyncReadFromSocket(socketIn);
                Thread t1 = asyncWriteToSocket(stdin, socketOut);
                t0.join();
                t1.join();
            } catch(InterruptedException | NoSuchElementException e){
                System.out.println("Connection closed from the client side");
            } finally {
                stdin.close();
                socketIn.close();
                socketOut.close();
                socket.close();
            }
        }
        catch (ConnectException ee){
            if (ee.getMessage().contains("Connection refused")) {
                System.out.println(Color.ANSI_RED + "[CRITICAL] Connection refused. Server probably down or full." + Color.RESET);
            }
            else  ee.getMessage();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void run(String s) throws IOException {
        ip = s;
        new Thread(this).start();
    }

}
