package it.polimi.ingsw.Network;


import it.polimi.ingsw.Model.Exceptions.NickAlreadyTakenException;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.News;
import it.polimi.ingsw.Model.Player;
import it.polimi.ingsw.Network.Messages.MOTD;
import it.polimi.ingsw.Network.Messages.ServerMessage;
import it.polimi.ingsw.ServerMain;
import it.polimi.ingsw.Utility.Pair;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class SocketClientConnection implements Runnable {

    private final Socket socket;
    private ObjectOutputStream out;
    private final Server server;
    /**
     * Listener helper object.
     **/
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private News news;
    private Player player;
    private boolean ready = false;

    private boolean active = true;

    /**
     * {@inheritDoc}
     */
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    /**
     * {@inheritDoc}
     */
    public SocketClientConnection(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }


    /**
     * Sets the player's ready state.
     */
    synchronized public void setReady() {
        this.ready = true;
        notify();
    }

    public void setPlayer(Player player) {
        if (this.player == null && player != null) this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    private synchronized boolean isActive() {
        return active;
    }

    /**
     * Method responsible for sending messages to a client.
     *
     * @param message Message to be sent.
     */
    public synchronized void send(Object message) {
        try {
            out.reset();
            out.writeObject(message);
            out.flush();
        } catch (IOException | NullPointerException ignored) {
        }

    }

    /**
     * Closes connection with the respective client.
     */
    public synchronized void closeConnection() {
        try {
            if (!graceful) {
                send(ServerMessage.connClosed);
            }
            socket.close();
            this.close();
        } catch (IOException e) {
            System.err.println("Error while closing socket!");
        }
        active = false;
    }

    private boolean graceful = false;

    public synchronized void setGracefulClose() {
        graceful = true;
    }

    /**
     * Calls {@link Server#deregisterConnection(SocketClientConnection)} to remove the connection from the server's list.
     */
    private void close() {
        System.out.println("Deregistering client...");
        Server.deregisterConnection(this);
        System.out.println("Done!");
    }

    /**
     * Asks for builder coordinates
     *
     * @param choices list of previously chosen coordinates
     * @return pair of chosen coordinates
     */
    synchronized Pair getBuilderChoice(ArrayList<Pair> choices) {
        Pair out;
        int c, r; //sia righe che colonne vanno da 0 a 4 compresi
        Scanner in = null;
        try {
            in = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            if (ServerMain.verbose()) e.printStackTrace();
        }
        while (true) {
            assert in != null;
            send("[CHOICE]@@@POS");
            while(true) {
                try {
                    String s = in.nextLine();
                    String[] inputs = s.split(",");
                    if (inputs.length != 2) throw new InputMismatchException();
                    r = Integer.parseInt(inputs[0]);
                    c = Integer.parseInt(inputs[1]);
                    if (r >= 0 && r < 5) {
                        if (c >= 0 && c < 5) break;
                        else send(ServerMessage.wrongNumber);
                    } else {
                        send(ServerMessage.wrongNumber);
                    }
                }
                catch (InputMismatchException | NumberFormatException e) {
                    send(ServerMessage.wrongNumber);
                }
            }
            out = new Pair(r,c);
            if (!choices.contains(out)) {
                break;
            } else {
                send(ServerMessage.cellNotAvailable);
            }
        }
        return out;
    }

    /**
     * Asks for god choice
     *
     * @param gods list of previously choosen by the first player
     * @return integer index of the choosen god
     */
    synchronized int getGodChoice(ArrayList<Integer> gods) {
        StringBuilder s = new StringBuilder();
        s.append("[CHOICE]@@@");
        for (Integer god : gods) {
            s.append(god).append("@@@");
        }
        send(s.toString());
        Scanner in = null;
        try {
            in = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            if (ServerMain.verbose()) e.printStackTrace();
        }
        assert in != null;
        int choice = -1;

        while(true){
            try{
                choice = in.nextInt();
                choice -= 1 ;
                if (choice < GameTable.completeGodList.size() && choice >= 0 && gods.contains(choice)) {
                    gods.remove(Integer.valueOf(choice));
                    send("You choose: " + GameTable.getCompleteGodList().get(choice));
                    return choice;
                } else {
                    send("Wrong number. Try again: ");
                }
            }
            catch (InputMismatchException e){
                send("Wrong number. Try again: ");
                in.nextLine();
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
            if (ServerMain.verbose()) e.printStackTrace();
        }
        assert in != null;
        int playersNumber;
        while(true){
            try {
                playersNumber = Integer.parseInt(in.nextLine());
                if (playersNumber == 2 || playersNumber == 3){
                    break;
                }
            }
            catch (NumberFormatException e){
                send("Wrong input. Please try again: ");
            }
        }
        send("[CHOICE]@@@GODS@@@" + playersNumber);
        int count = 0;
        ArrayList<Integer> gods =  new ArrayList<>();
        while(count < playersNumber){
            try{
                int i = Integer.parseInt(in.nextLine());
                i -= 1 ;
                if (i < GameTable.completeGodList.size() && i >= 0 && !gods.contains(i)) {
                    gods.add(count, i);
                    count += 1;
                }
            }
            catch (NumberFormatException ignored){
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
            socket.setPerformancePreferences(0, 1, 0);
            socket.setTcpNoDelay(true);
            in = new Scanner(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            send(ServerMessage.welcome);
            send(new MOTD(Server.getMOTD()));
            if (ServerMain.persistence()) {
                send(ServerMessage.reloadGameChoice);
            } else send(ServerMessage.welcomeNoPersistence);
            String read = in.nextLine();
            if (ServerMain.persistence() && read.equals("R")) {
                send("Please enter game number: ");
                int gameNumber = 0;
                try {
                    gameNumber = Integer.parseInt(in.nextLine());
                } catch (NumberFormatException e) {
                    send(ServerMessage.invalidNumber);
                    closeConnection();
                }
                send("Please enter your previous nickname: ");
                String nick = in.nextLine();
                try {
                    server.lobbyFromDisk(this, nick, gameNumber);
                } catch (Exception e) {
                    send("Invalid game index / nickname.");
                    closeConnection();
                }
            } else {
                name = read;
                while (true) {
                    try {
                        server.lobby(this, name);
                        break;
                    } catch (NickAlreadyTakenException ee) {
                        send(ServerMessage.userAlreadyTaken);
                        read = in.nextLine();
                        name = read;
                    }
                }
            }
            while (isActive()) {
                if (ready) {
                    read = in.nextLine();
                    setNews(new News(read, this), "INPUT");
                } else {
                    synchronized (this) {
                        wait();
                    }
                }
            }
        } catch (NoSuchElementException e) {
            if (!graceful) {
                System.err.println("Player " + this.getPlayer().getNickname() + " closed connection. Closing game...");
                setNews(new News(null, this), "ABORT");
            }
        } catch (Exception e) {
            if (!graceful) {
                System.err.println("Exception thrown in SocketClientConnection. Closing game...");
                if (ServerMain.verbose()) e.printStackTrace();
                setNews(new News(null, this), "ABORT");
            }
        } finally {
            closeConnection();
        }
    }

    private void setNews(News news, String type) {
        support.firePropertyChange(type, this.news, news);
        this.news = news;
    }
}
