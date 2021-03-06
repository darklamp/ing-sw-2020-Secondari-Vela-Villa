/*
 * Santorini
 * Copyright (C)  2020  Alessandro Villa and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Affero General Public License as
 *      published by the Free Software Foundation, either version 3 of the
 *      License, or (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Affero General Public License for more details.
 *
 *      You should have received a copy of the GNU Affero General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * E-mail contact addresses:
 * darklampz@gmail.com
 * alessandro17.villa@mail.polimi.it
 *
 */

package it.polimi.ingsw.Client;

import it.polimi.ingsw.Network.Messages.GameStateMessage;
import it.polimi.ingsw.Network.Messages.MOTD;
import it.polimi.ingsw.Network.Messages.Message;
import it.polimi.ingsw.View.CellView;

import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static it.polimi.ingsw.Client.ClientState.*;
import static java.lang.Thread.sleep;

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

    private static long gameTimer;

    public static void setTimer(long timer) {
        Client.gameTimer = timer;
    }

    public static long getGameTimer() {
        return gameTimer;
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

    public static GameStateMessage getLastStateMessage() {
        return lastStateMessage;
    }

    private static GameStateMessage lastStateMessage;

    private static InetAddress ip;

    public static void setIp(InetAddress ip) {
        Client.ip = ip;
    }

    private static int god;

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        Client.port = port;
    }

    private static int port;

    public synchronized static ClientState getState() {
        return state;
    }

    public synchronized static void setState(ClientState state) {
        Client.state = state;
    }

    private static ClientState state = ClientState.INIT;
    public static List<String> completeGodList = Arrays.asList("APOLLO", "ARTEMIS", "ATHENA", "ATLAS", "DEMETER", "HEPHAESTUS", "MINOTAUR", "PAN", "PROMETHEUS"); /* list containing all the basic gods */

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
     * Filters what to accept for serialization and what not to accept.
     * Note: not needed server-side since the server accepts only strings.
     */
    private static final ObjectInputFilter serializationFilter = ObjectInputFilter.Config.createFilter("it.polimi.ingsw.**;java.**;!*");

    /**
     * Reads news from socket connection and calls Ui method on it
     *
     * @param socketIn input socket ObjectStream
     * @return Thread
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
                        if (inputObject instanceof MOTD) {
                            completeGodList = ((MOTD) inputObject).getGods();
                            ui.process((Message) inputObject);
                        } else if (inputObject instanceof GameStateMessage) {
                            GameStateMessage c = (GameStateMessage) inputObject;
                            if (verbose())
                                System.out.println("[DEBUG] New client states are: " + c.get(0) + "," + c.get(1) + "," + c.get(2));
                            state = parseState(c);
                            ui.processTurnChange(state);
                        } else ui.process((Message) inputObject);
                    }
                }
            } catch (InvalidClassException e) {
                System.err.println("Invalid class received. Filtered out.");
            } catch (Exception e) {
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
     * @param stdin System.in scanner
     * @param socketOut output
     * @return Thread
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
            ExecutorService executor = Executors.newSingleThreadExecutor();
            AtomicReference<Socket> socket = new AtomicReference<>();
            Future<?> t11 = executor.submit(() -> {
                try {
                    socket.set(new Socket(ip, port));
                } catch (IOException e) {
                    throw new RuntimeException();
                }
            });
            try {
                t11.get(3, TimeUnit.SECONDS);
            } catch (Exception e) {
                throw new ConnectException("Connection refused");
            }

            socket.get().setPerformancePreferences(0, 1, 0);
            socket.get().setTcpNoDelay(true);
            ui.process("Connection established");
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    socket.get().close();
                } catch (IOException ignored) {
                }
            }));
            try (Scanner stdin = new Scanner(System.in); PrintWriter socketOut = new PrintWriter(socket.get().getOutputStream()); ObjectInputStream socketIn = new ObjectInputStream(socket.get().getInputStream())) {
                socketIn.setObjectInputFilter(serializationFilter);
                Thread t0 = asyncReadFromSocket(socketIn);
                Thread t1 = asyncWriteToSocket(stdin, socketOut);
                t0.join();
                t1.join();
            } catch (InterruptedException | NoSuchElementException e) {
                System.out.println("Connection closed from the client side");
            } finally {
                socket.get().close();
            }
        } catch (ConnectException ee) {
            if (ee.getMessage().contains("Connection refused"))
                ui.process("[CRITICAL] Connection refused.\nServer probably down or full.\nExiting..");
            else
                ui.process(ee.getMessage());
            try {
                sleep(2400);
            } catch (InterruptedException ignored) {
            }
            System.exit(-1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ClientState parseState(GameStateMessage s) {
        lastStateMessage = s;
        currentPlayer = s.getCurrentPlayer();
        return s.get(playerIndex);
    }

}

