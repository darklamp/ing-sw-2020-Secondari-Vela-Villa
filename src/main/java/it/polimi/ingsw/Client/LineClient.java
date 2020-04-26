package it.polimi.ingsw.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class LineClient {
    private String ip,id,s;
    private int port,x,r,c;
    Scanner input;
    private CellView[][] MatrixCell;

    public LineClient(String ip, int port){
        this.ip = ip;
        this.port = port;
        MatrixCell=new CellView[5][5];
    }

    public Thread asyncReadFromSocket(final ObjectInputStream socketIn){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (isActive()) {
                        Object inputObject = socketIn.readObject();
                        if(inputObject instanceof String){
                            System.out.println((String)inputObject);
                        } /*else if (inputObject instanceof Board){
                            ((Board)inputObject).print();
                        } */else {
                            throw new IllegalArgumentException();
                        }
                    }
                } catch (Exception e){
                    setActive(false);
                }
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



    public void startClient() throws IOException {

        Socket socket = new Socket(ip, port);
        System.out.println("Connection established");
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
      /*  try{
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
        }*/
    }
    public void ShowTable() throws NullCellException {
        for(r=0,r<5,r++)
        {
            for(c=0,c<5,c++)
            {
                if(r==0 & c==0)
                {
                    System.out.println("   a   b   c   d   e\n\n\n0  ");
                }
                if(r==1 & c==0)
                {
                    System.out.println("1  ");
                }
                if(r==2 & c==0)
                {
                    System.out.println("2  ");
                }
                if(r==3 & c==0)
                {
                    System.out.println("3  ");
                }
                if(r==4 & c==0)
                {
                    System.out.println("4  ");
                }
                System.out.println("4  ");

            }
        }
    }

}
