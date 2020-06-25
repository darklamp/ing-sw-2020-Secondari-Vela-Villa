package it.polimi.ingsw.Network;


import it.polimi.ingsw.Model.Exceptions.NickAlreadyTakenException;
import it.polimi.ingsw.Model.News;
import it.polimi.ingsw.Model.Player;
import it.polimi.ingsw.Network.Messages.MOTD;
import it.polimi.ingsw.Network.Messages.ServerMessage;
import it.polimi.ingsw.ServerMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Class representation of the socket connection between the server and a specific client.
 */
public class SocketClientConnection implements Runnable {


    private final Socket socket;
    private ObjectOutputStream out;
    private final Server server;
    private final Logger logger = LoggerFactory.getLogger(SocketClientConnection.class);

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

    public SocketClientConnection(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    Scanner getScanner() throws IOException {
        return new Scanner(socket.getInputStream());
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
        } catch (Exception ignored) {
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
            logger.error("Error while closing socket!");
        }
        active = false;
    }

    private boolean graceful = false;

    /**
     * Sets a graceful state, which means when the socket connection gets closed, the exception thrown doesn't lead
     * to the game being closed.
     */
    public synchronized void setGracefulClose() {
        graceful = true;
    }

    /**
     * Calls {@link Server#deregisterConnection(SocketClientConnection)} to remove the connection from the server's list.
     */
    private void close() {
        Server.deregisterConnection(this);
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

            send(new MOTD(Server.getMOTD()));
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
                logger.info("Player {}'s connection dropped. Closing game...", this.getPlayer().getNickname());
                setNews(new News(null, this), "ABORT");
            }
        } catch (Exception e) {
            if (!graceful) {
                logger.info("Exception thrown in SocketClientConnection of player {} (probably dc'ed). Closing game.", this.getPlayer().getNickname());
                logger.debug("StackTrace:\n", e);
                setNews(new News(null, this), "ABORT");
            }
        } finally {
            if (graceful) logger.info("Gracefully closing {}'s connection...", this.getPlayer().getNickname());
            closeConnection();
        }
    }

    private void setNews(News news, String type) {
        support.firePropertyChange(type, this.news, news);
        this.news = news;
    }
}
