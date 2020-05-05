package it.polimi.ingsw.Network;


import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.News;
import it.polimi.ingsw.Model.Pair;
import it.polimi.ingsw.Model.Player;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class SocketClientConnection implements Runnable {

    private final Socket socket;
    private ObjectOutputStream out;
    private final Server server;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this); /** Listener helper object **/
    private News news;
    private Player player;
    private boolean ready = false;

    private boolean active = true;

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public SocketClientConnection(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    public void setReady(){
        ClientState newState = ClientState.WAIT;
        this.send(newState);
        this.ready = true;
    }

    public void setState(ClientState state){
        this.send(state);
    }

    public void setPlayer(Player player) {
        if (this.player == null && player != null) this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    private synchronized boolean isActive(){
        return active;
    }

    public synchronized void send(Object message) {
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
            this.close();
        } catch (IOException e) {
            System.err.println("Error when closing socket!");
        }
        active = false;
    }

    private void close() {
        System.out.println("Deregistering client...");
        Server.deregisterConnection(this);
        System.out.println("Done!");
    }

    public void asyncSend(final Object message){
        new Thread(() -> send(message)).start();
    }

    /**
     * Asks for builder coordinates
     * @param choices list of previously chosen coordinates
     * @return pair of chosen coordinates
     */
    public synchronized Pair getBuilderChoice(ArrayList<Pair> choices){
        Pair out;
        int c,r; //sia righe che colonne vanno da 0 a 4 compresi
        Scanner in = null;
        try {
            in = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(true){
            int x=0;
            assert in != null;
            send("Insert a row number >= 0 and < 5: ");
            while(true) {
                r = in.nextInt();
                if(r >= 0 && r < 5){
                    break;
                }
                else{
                    send(ServerMessage.wrongNumber);
                }
            }
            send("Insert a column >= 0 and < 5: ");
            while(true) {
                c = in.nextInt();
                if(c >= 0 && c < 5){
                    break;
                }
                else{
                    send(ServerMessage.wrongNumber);
                }
            }
            out = new Pair(r,c);
            for (Pair pair : choices){
                if (out.equals(pair)){
                    x++;
                }
            }
            if(x==0){
                break;
            }
            else{
                send("This cell is not available, try another one");
            }
        }
        return out;
    }

    public synchronized int getGodChoice(ArrayList<Integer> gods){
        for (Integer god : gods) {
            send(god + ") " + GameTable.getCompleteGodList().get(god) + "\n");
        }
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
        send(ServerMessage.firstPlayer);
        Scanner in = null;
        try {
            in = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert in != null;
        int playersNumber = in.nextInt();
        while (playersNumber != 2 && playersNumber != 3){
            send(ServerMessage.firstPlayer);
            playersNumber = in.nextInt();
        }
        send("Nice! Now you need to choose " + playersNumber + " gods to be used in the game.\n");
        AtomicInteger counter = new AtomicInteger(0);
        GameTable.getCompleteGodList().forEach(name -> send(counter.getAndIncrement() + ") " + name + "\n"));
        int count = 0;
        ArrayList<Integer> gods =  new ArrayList<>();
        while(count < playersNumber){
            send(ServerMessage.nextNumber);
            int i = in.nextInt();
            if (i < 10 && i >= 0 && !gods.contains(i)) {
                gods.add(count,i);
                count += 1;
            }
        }
        gods.add(0,playersNumber);
        if (playersNumber == 2){
            send("Wait for another player...");
        }
        else {
            send("Wait for 2 more players...");
        }
        return gods;
    }

    @Override
    public void run() {
        Scanner in;
        String name;
        try{
            in = new Scanner(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            send(ServerMessage.welcome);
            String read = in.nextLine();
            name = read;
            while(true){
                try{
                    server.lobby(this, name);
                    break;
                }
                catch (Exception ee){
                    send(ServerMessage.userAlreadyTaken);
                    read = in.nextLine();
                    name = read;
                }
            }
            while(isActive()){
                if (ready && player.getState() != ClientState.WAIT){
                    ExecutorService service = Executors.newSingleThreadExecutor();

                    try {
                        AtomicReference<String> read1 = new AtomicReference<>();
                        Runnable r = () -> read1.set(in.nextLine());
                        Future<?> f = service.submit(r);
                        f.get(5, TimeUnit.MINUTES);
                        setNews(new News(read1.toString(),this),"INPUT");
                    }
                    catch (final InterruptedException | ExecutionException ignored) {
                    }
                    catch (final TimeoutException e) {
                        this.send("Disconnecting for inactivity ...");
                        setNews(new News(null,this),"PLAYERTIMEOUT");
                    }
                    finally {
                        service.shutdown();
                    }
                }
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
