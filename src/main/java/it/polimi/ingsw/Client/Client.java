package it.polimi.ingsw.Client;

import it.polimi.ingsw.Network.Messages.GameStateMessage;
import it.polimi.ingsw.Network.Messages.Message;
import it.polimi.ingsw.View.CellView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static it.polimi.ingsw.Client.ClientState.*;

public class Client implements Runnable {

    public static boolean verbose() {
        return debug;
    }

    private static boolean debug = false;

    public static int[] getGods() {
        return gods;
    }

    public static void setGods(int god1, int god2, int god3) {
        Client.gods[0] = god1;
        Client.gods[1] = god2;
        Client.gods[2] = god3;
    }

    private static int[] gods = new int[3];

    public static int getPlayerIndex() {
        return playerIndex;
    }

    public static void setPlayerIndex(int playerIndex) {
        Client.playerIndex = playerIndex;
    }

    public static int getPlayersNumber() {
        return playersNumber;
    }

    public static void setPlayersNumber(int playersNumber) {
        Client.playersNumber = playersNumber;
    }

    public static String getCurrentPlayer() {
        return currentPlayer;
    }

    static public int getGod() {
        return god;
    }

    static public void setGod(int god) {
        Client.god = god;
    }

    private static int playerIndex, playersNumber;

    private static String currentPlayer;

    private static InetAddress ip;

    public static void setIp(InetAddress ip) {
        Client.ip = ip;
    }

    private static int god;
    private static int port;

    public synchronized static ClientState getState() {
        return state;
    }

    public synchronized static void setState(ClientState state) {
        Client.state = state;
    }

    private static ClientState state = ClientState.INIT;
    public static final List<String> completeGodList = Arrays.asList("APOLLO", "ARTEMIS", "ATHENA", "ATLAS", "DEMETER", "HEPHAESTUS", "MINOTAUR", "PAN", "PROMETEUS"); /* list containing all the basic gods */

    private static Ui ui;

    private Client(int port) {
        Client.port = port;
    }

    public Client(InetAddress ip, int port, boolean debug) {
        this(port);
        Client.ip = ip;
        Client.debug = debug;
    }

    static void setUi(Ui ui) {
        Client.ui = ui;
    }

    /**
     * Reads news from socket connection and calls Ui method on it
     */
    public synchronized Thread asyncReadFromSocket(final ObjectInputStream socketIn) {
        Thread t = new Thread(() -> {
            try {
                while (isActive()) {
                    Object inputObject = socketIn.readObject();
                    if (verbose()) System.out.println("[DEBUG] Recv input: " + inputObject.getClass());
                    if (inputObject instanceof String) {
                        ui.process((String) inputObject);
                    } else if (inputObject instanceof CellView[][]) {
                        ui.showTable((CellView[][]) inputObject);
                    } else if (inputObject instanceof Message) {
                        if (inputObject instanceof GameStateMessage) {
                            GameStateMessage c = (GameStateMessage) inputObject;
                            state = parseState(c);
                            ui.processTurnChange(state);
                        } else ui.process((Message) inputObject);
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

    /**
     * Ask for input -> verify it -> send it to server via socket
     */
    public synchronized Thread asyncWriteToSocket(final Scanner stdin, final PrintWriter socketOut) {
        Thread t = new Thread(() -> {
            try {
                while (isActive()) {
                    String inputLine = ui.nextLine(stdin);
                    switch (state) {
                        case INIT -> socketOut.println(inputLine);
                        case BUILDORPASS -> {
                            try {
                                boolean pass = true;
                                if (inputLine.toUpperCase().contains("B")) pass = false;
                                if (!pass) {
                                    state = BUILD;
                                    ui.processTurnChange(BUILD);
                                } else {
                                    state = WAIT;
                                    socketOut.println("PASS");
                                }
                            } catch (Exception ignored) {
                            }
                        }
                        case MOVEORBUILD -> {
                            try {
                                boolean move = true;
                                if (inputLine.toUpperCase().contains("B")) move = false;
                                if (move) {
                                    state = MOVE;
                                    ui.processTurnChange(MOVE);
                                } else {
                                    state = BUILD;
                                    ui.processTurnChange(BUILD);
                                }
                            } catch (Exception ignored) {
                            }
                        }
                        case MOVE -> {
                            try {
                                String[] s = inputLine.split(",");
                                socketOut.println("MOVE" + "@@@" + s[0] + "@@@" + s[1] + "@@@" + s[2]);
                                if (Client.verbose())
                                    System.out.println("[DEBUG] SENT: " + "MOVE" + "@@@" + s[0] + "@@@" + s[1] + "@@@" + s[2]);
                            } catch (Exception ignored) {
                            }
                        }
                        case BUILD -> {
                            try {
                                String[] s = inputLine.split(",");
                                String out = "BUILD" + "@@@" + s[0] + "@@@" + s[1] + "@@@" + s[2];
                                if (s.length == 4) {
                                    out += "@@@";
                                    out += s[3];
                                }
                                if (Client.verbose()) System.out.println("[DEBUG] SENT: " + out);
                                socketOut.println(out);
                            } catch (Exception ignored) {
                            }
                        }
                    }
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
            socket.setPerformancePreferences(0, 1, 0);
            socket.setTcpNoDelay(true);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }));
            ui.process("Connection established");
            Scanner stdin = new Scanner(System.in);
            PrintWriter socketOut = new PrintWriter(socket.getOutputStream());
            ObjectInputStream socketIn = new ObjectInputStream(socket.getInputStream());
            try {
                Thread t0 = asyncReadFromSocket(socketIn);
                Thread t1 = asyncWriteToSocket(stdin, socketOut);
                t0.join();
                t1.join();
            } catch (InterruptedException | NoSuchElementException e) {
                System.out.println("Connection closed from the client side");
            } finally {
                stdin.close();
                socketIn.close();
                socketOut.close();
                socket.close();
            }
        } catch (ConnectException ee) {
            if (ee.getMessage().contains("Connection refused")) {
                System.err.println("[CRITICAL] Connection refused. Server probably down or full.");
            } else ee.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ClientState parseState(GameStateMessage s) {
        currentPlayer = s.getCurrentPlayer();
        return s.get(playerIndex);
    }

}

