package it.polimi.ingsw.Network;


import it.polimi.ingsw.Model.Exceptions.NickAlreadyTakenException;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.News;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SocketClientConnection implements Runnable {

    private final Socket socket;
    private ObjectOutputStream out;
    private final Server server;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this); /** Listener helper object **/
    private News news;

    private boolean active = true;

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public SocketClientConnection(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    private synchronized boolean isActive(){
        return active;
    }

    synchronized void send(Object message) {
        try {
            out.reset();
            out.writeObject(message);
            out.flush();
        } catch(IOException e){
            System.err.println(e.getMessage());
        }

    }

    public synchronized void closeConnection() {
        send("Connection closed!");
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error when closing socket!");
        }
        active = false;
    }

    private void close() {
        closeConnection();
        System.out.println("Deregistering client...");
      //  server.deregisterConnection(this);
        System.out.println("Done!");
    }

    public void asyncSend(final Object message){
        new Thread(() -> send(message)).start();
    }

    synchronized int getGodChoice(ArrayList<Integer> gods){
        Scanner in = null;
        try {
            in = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert in != null;
        send("Please choose one: ");
        int choice = in.nextInt(); //possibile bug qua
        while(true){
            if (choice < 10 && choice >= 0 && gods.contains(choice)){
                gods.remove(Integer.valueOf(choice));
                send("You choose: " + GameTable.getCompleteGodList().get(choice));
                return choice;
            }
            else{
                send("Wrong number. Try again: ");
                choice = in.nextInt();
            }
        }

    }

    /**
     * Asks the first player to input the playerNumber and the choice of gods.
     * @return array of integers; in the first position resides playerNumber, while the next 2/3 positions contain the gods
     */
    synchronized ArrayList<Integer> firstPlayer() {
        send("Looks like you're the first player to connect. You get to decide the number of players.\nPlease input a natural lower than 4 and higher than 1: ");
        Scanner in = null;
        try {
            in = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert in != null;
        int playersNumber = in.nextInt();
        while (playersNumber != 2 && playersNumber != 3){
            send("Looks like you're the first to connect. You get to decide the number of players.\nPlease input a natural lower than 4 and higher than 1: ");
            playersNumber = in.nextInt();
        }
        send("Nice! Now you need to choose " + playersNumber + " gods to be used in the game.\n");
        AtomicInteger counter = new AtomicInteger(0);
        GameTable.getCompleteGodList().forEach(name -> send(counter.getAndIncrement() + ") " + name + "\n"));
        int count = 0;
        ArrayList<Integer> gods =  new ArrayList<>();
        while(count < playersNumber){
            send("Please input the next number: ");
            int i = in.nextInt();
            if (i < 10 && i >= 0 && !gods.contains(i)) {
                gods.add(count,i);
                count += 1;
            }
        }
        gods.add(0,playersNumber);
        return gods;
    }

    @Override
    public void run() {
        Scanner in;
        String name;
        try{
            in = new Scanner(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            send("Welcome!\nWhat's your name?");
            String read = in.nextLine();
            name = read;
            while(true){
                try{
                    server.lobby(this, name);
                    break;
                }
                catch (Exception ee){
                    send("Username already taken. Please enter a different one: ");
                    read = in.nextLine();
                    name = read;
                }
            }
            while(isActive()){
                read = in.nextLine();
                setNews(new News(),"INPUT");
            }
        } catch (IOException | NoSuchElementException e) {
            System.err.println("Error!" + e.getMessage());
        }finally{
            close();
        }
    }

    public void setNews(News news, String type) {
        support.firePropertyChange(type, this.news, news);
        this.news = news;
    }
}
