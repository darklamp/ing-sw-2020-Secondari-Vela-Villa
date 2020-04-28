package it.polimi.ingsw.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Client implements Runnable {
    private static String ip,id,s;
    private int port,x,r,c;
    Scanner input;
    private Color blu,green,yellow.red;
    private Dice dice;
    private static Ui ui;

    public Client(int port){
        this.port = port;
        r=0;
        c=0;
        blue = Color.ANSI_BLUE;
        green = Color.ANSI_GREEN;
        yellow = Color.ANSI_YELLOW;
        red = Color.ANSI_RED;
        dice = new Dice();
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
                    } /*else if (inputObject instanceof Board){
                        ((Board)inputObject).print();
                    } */else {
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
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void run(String s) throws IOException {
        ip = s;
        new Thread(this).start();
    }

    /*public void ShowTable(CellView[][] matrix) throws NullCellException {
        for(r=0,r<5,r++)
        {
            for(c=0,c<5,c++)
            {
                if(r==0 & c==0)
                {
                    System.out.print("   a  b  c  d  e\n\n\n0  ");
                }
                if(r==1 & c==0)
                {
                    System.out.print("1  ");
                }
                if(r==2 & c==0)
                {
                    System.out.print("2  ");
                }
                if(r==3 & c==0)
                {
                    System.out.print("3  ");
                }
                if(r==4 & c==0)
                {
                    System.out.print("4  ");
                }
                if(matrix[r][c].height == NONE){
                    dice.Zero();
                    if(matrix[r][c].player == -1){
                        dice.SetColor=(yellow);
                        dice.Stamp;
                    }
                    if(matrix[r][c].player == 0){
                        dice.SetColor=(blue);
                        dice.Stamp;
                    }
                    if(matrix[r][c].player == 1){
                        dice.SetColor=(red);
                        dice.Stamp;
                    };
                    if(matrix[r][c].player == 2){
                        dice.SetColor=(green);
                        dice.Stamp;
                    }
                    }
                if(matrix[r][c].height == BASE){
                    dice.One();
                    if(matrix[r][c].player == -1){
                        dice.SetColor=(yellow);
                        dice.Stamp;
                    }
                    if(matrix[r][c].player == 0){
                        dice.SetColor=(blue);
                        dice.Stamp;
                    }
                    if(matrix[r][c].player == 1){
                        dice.SetColor=(red);
                        dice.Stamp;
                    };
                    if(matrix[r][c].player == 2){
                        dice.SetColor=(green);
                        dice.Stamp;
                    }
                    }
                if(matrix[r][c].height == MIDDLE){
                    dice.Two();
                    if(matrix[r][c].player == -1){
                        dice.SetColor=(yellow);
                        dice.Stamp;
                    }
                    if(matrix[r][c].player == 0){
                        dice.SetColor=(blue);
                        dice.Stamp;
                    }
                    if(matrix[r][c].player == 1){
                        dice.SetColor=(red);
                        dice.Stamp;
                    };
                    if(matrix[r][c].player == 2){
                        dice.SetColor=(green);
                        dice.Stamp;
                    }
                    }
                if(matrix[r][c].height == TOP){
                    dice.Three();
                    if(matrix[r][c].player == -1){
                        dice.SetColor=(yellow);
                        dice.Stamp;
                    }
                    if(matrix[r][c].player == 0){
                        dice.SetColor=(blue);
                        dice.Stamp;
                    }
                    if(matrix[r][c].player == 1){
                        dice.SetColor=(red);
                        dice.Stamp;
                    };
                    if(matrix[r][c].player == 2){
                        dice.SetColor=(green);
                        dice.Stamp;
                    }
                    }
                if(matrix[r][c].height == DOME){
                    dice.Four();
                    if(matrix[r][c].player == -1){
                        dice.SetColor=(yellow);
                        dice.Stamp;
                    }
                    if(matrix[r][c].player == 0){
                        dice.SetColor=(blue);
                        dice.Stamp;
                    }
                    if(matrix[r][c].player == 1){
                        dice.SetColor=(red);
                        dice.Stamp;
                    };
                    if(matrix[r][c].player == 2){
                        dice.SetColor=(green);
                        dice.Stamp;
                    }
                    }


            }
        }
    }*/

}
