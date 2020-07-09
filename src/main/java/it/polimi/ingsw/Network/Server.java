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

package it.polimi.ingsw.Network;

import it.polimi.ingsw.Controller.MainController;
import it.polimi.ingsw.Model.Exceptions.NickAlreadyTakenException;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.Player;
import it.polimi.ingsw.Network.Exceptions.BrokenLobbyException;
import it.polimi.ingsw.Network.Messages.ServerMessage;
import it.polimi.ingsw.ServerMain;
import it.polimi.ingsw.Utility.Color;
import it.polimi.ingsw.View.ServerView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {
    private ServerSocket serverSocket;
    private final ExecutorService executor = Executors.newFixedThreadPool(128);
    /**
     * Thread pool creator
     **/
    private static final Map<String, SocketClientConnection> waitingConnection = new LinkedHashMap<>();
    /**
     * contains player connections waiting to be matchmade
     **/
    private static final Map<Integer, ArrayList<SocketClientConnection>> gameList = new LinkedHashMap<>();
    /**
     * Integer contains the game index, ArrayList contains client connections for the respective game
     **/
    private static final Map<Integer, ArrayList<Integer>> gameProperties = new LinkedHashMap<>();
    /**
     * position [0] contains the number of players for the game; positions [1..2/3] contain the chosen gods
     **/
    private static final Map<Integer, MainController> gameControllers = new LinkedHashMap<>();
    /**
     * contains main controllers
     **/
    private static int currentGameIndex = 0;

    /**
     * @return time in milliseconds that a player is allowed (on a per turn basis)
     */
    public static long getMoveTimer() {
        return moveTimerTimeUnit.toMillis(moveTimer);
    }

    /**
     * index of game currently in the process of being created; eg: if it's set to 1 it means game 0 is already started / finished, while game 1 is being made
     **/
    private static short moveTimer = (short) 2;
    private static TimeUnit moveTimerTimeUnit = TimeUnit.MINUTES;

    private final Logger logger = LoggerFactory.getLogger(Server.class);

    public static String getMOTD() {
        return MOTD;
    }

    /**
     * Message of the day
     */
    private static String MOTD = "Have fun!";

    public static ServerView getServerView() {
        return serverView;
    }

    private static ServerView serverView;

    /**
     * @return index of game currently in the process of being created
     * @example there are 4 active games, with indexes 0..3, and the first plauer of the 5th game
     * has entered the lobby --> the current index is 4.
     */
    public synchronized static int getCurrentGameIndex() {
        return currentGameIndex;
    }

    /**
     * @param c is the client kicked from server
     */
    synchronized static void deregisterConnection(SocketClientConnection c) {
        for (Map.Entry<Integer, ArrayList<SocketClientConnection>> entry : gameList.entrySet()) {
            try {
                for (SocketClientConnection connection : entry.getValue()) {
                    if (c == connection) {
                        entry.getValue().remove(c);
                        break;
                    }
                }
            } catch (NullPointerException ignored) {
            }
        }
    }

    /**
     * Asks the first player to input the playerNumber and the choice of gods.
     * NB: in case the maximum number of players is 2, the function does not ask for the number of players.
     *
     * @param c connection of first player
     * @return array of integers; in the first position resides playerNumber, while the next 2/3 positions contain the gods
     */
    synchronized ArrayList<Integer> firstPlayer(SocketClientConnection c) {
        Scanner in = null;
        try {
            in = c.getScanner();
        } catch (IOException e) {
            logger.error("Error during first player's choices.", e);
        }
        assert in != null;
        int playersNumber;
        if (ServerMain.getMaxPlayersNumber() != 2) {
            c.send(ServerMessage.firstPlayer);
            while (true) {
                try {
                    playersNumber = Integer.parseInt(in.nextLine());
                    if (playersNumber >= 2 && playersNumber <= ServerMain.getMaxPlayersNumber()) {
                        break;
                    }
                } catch (NumberFormatException e) {
                    c.send("Wrong input. Please try again: ");
                }
            }
        } else playersNumber = 2;
        c.send("[CHOICE]@@@GODS@@@" + playersNumber);
        int count = 0;
        ArrayList<Integer> gods = new ArrayList<>();
        while (count < playersNumber) {
            try {
                int i = Integer.parseInt(in.nextLine());
                i -= 1;
                if (i < GameTable.completeGodList.size() && i >= 0 && !gods.contains(i)) {
                    gods.add(count, i);
                    count += 1;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        gods.add(0, playersNumber);
        if (playersNumber == 2) {
            c.send("Wait for another player...");
        } else {
            c.send("Wait for 2 more players...");
        }
        return gods;
    }


    /**
     * @param c    is the client connection
     * @param name is the Nickname
     * @throws Exception, in this case the lobby is reset
     *                    Handle the interaction with the first player and then wait for the other player(s), once the game
     *                    is started the lobby is emptied and ready to "host" another game.
     */
    synchronized void lobby(SocketClientConnection c, String name) throws Exception {
        try {
            if (waitingConnection.containsKey(name) || name.equals("") || name.contains("\n"))
                throw new NickAlreadyTakenException();
            logger.info("User {} connected to game {}'s lobby.", name, getCurrentGameIndex());
            waitingConnection.put(name, c);
            if (waitingConnection.size() == 1) {
                ArrayList<Integer> gameProps = firstPlayer(c);
                ArrayList<SocketClientConnection> temp = new ArrayList<>();
                temp.add(c);
                gameList.put(getCurrentGameIndex(), temp);
                gameProperties.put(getCurrentGameIndex(), gameProps);
            } else if (!(waitingConnection.size() == gameProperties.get(getCurrentGameIndex()).get(0))) {
                ArrayList<SocketClientConnection> temp = gameList.get(getCurrentGameIndex());
                c.send("Wait for one more player..");
                temp.add(c);
            } else { // start game
                List<String> keys = new ArrayList<>(waitingConnection.keySet());
                SocketClientConnection c1 = waitingConnection.get(keys.get(0));
                SocketClientConnection c2 = waitingConnection.get(keys.get(1));
                SocketClientConnection c3 = null;
                if (waitingConnection.size() == 3) {
                    c3 = waitingConnection.get(keys.get(2));
                    c3.send("Wait for the first player to choose his god..");
                }
                ArrayList<Integer> gods = new ArrayList<>();
                for (int i = 1; i <= gameProperties.get(getCurrentGameIndex()).get(0); i++) {
                    gods.add(gameProperties.get(getCurrentGameIndex()).get(i));
                }
                GameTable gameTable = new GameTable(keys.size());
                MainController controller = new MainController(gameTable);
                Player player1 = new Player(keys.get(0), gameTable, c1);
                c1.setPlayer(player1);
                Player player2 = new Player(keys.get(1), gameTable, c2);
                c2.setPlayer(player2);
                Player player3 = null;
                if (c3 != null) {
                    player3 = new Player(keys.get(2), gameTable, c3);
                    c3.setPlayer(player3);
                }
                GameInitializer gameInitializer = new GameInitializer(c1, c2, c3, gods, player1, player2, player3, gameTable, controller);
                controller.setGameInitializer(gameInitializer);
                new Thread(gameInitializer).start();
                gameControllers.put(getCurrentGameIndex(), controller);
                ArrayList<SocketClientConnection> playingConnections = new ArrayList<>();
                playingConnections.add(c1);
                playingConnections.add(c2);
                if (c3 != null) playingConnections.add(c3);
                gameList.put(getCurrentGameIndex(), playingConnections);
                waitingConnection.clear();
                currentGameIndex += 1;
            }
        } catch (NickAlreadyTakenException e) {
            throw new NickAlreadyTakenException();
        } catch (Exception e) {
            clearLobby();
        }
    }

    private void clearLobby() throws BrokenLobbyException {
        waitingConnection.clear();
        if (gameList.get(getCurrentGameIndex()) == null) throw new BrokenLobbyException();
        if (gameList.size() > 0) gameList.get(getCurrentGameIndex()).clear();
        if (gameProperties.size() > 0) gameProperties.get(getCurrentGameIndex()).clear();
        throw new BrokenLobbyException();
    }

    private static final Map<Integer, ArrayList<String>> gameFromDiskList = new LinkedHashMap<>();

    /**
     * Same as {@link Server#lobby(SocketClientConnection, String)}, except it's used to re-create a game
     * loaded from disk.
     *
     * @param c         Client connection to be added
     * @param name      Client nickname to be added
     * @param gameIndex Index of game to be resumed
     * @throws NickAlreadyTakenException if a player with the same nickname is already in game
     */
    synchronized void lobbyFromDisk(SocketClientConnection c, String name, int gameIndex) throws NickAlreadyTakenException {
        int oldIndex = gameControllers.get(gameIndex).containsPlayer(name);
        if (oldIndex == -1) throw new NickAlreadyTakenException();
        else {
            ArrayList<SocketClientConnection> list = gameList.computeIfAbsent(gameIndex, k -> new ArrayList<>());
            ArrayList<String> list2 = gameFromDiskList.computeIfAbsent(gameIndex, k -> new ArrayList<>());
            if (list2.size() > 0) {
                if (list2.stream().anyMatch(c1 -> c1.equals(name))) throw new NickAlreadyTakenException();
            }
            if (oldIndex == 2) {
                list.add(c);
            } else if (oldIndex == 1) {
                if (list.size() == 0) list.add(c);
                else if (list.size() == 1) {
                    if (gameControllers.get(gameIndex).containsPlayer(gameFromDiskList.get(gameIndex).get(0)) == 0)
                        list.add(c);
                    else list.add(0, c);
                } else {
                    list.add(1, c);
                }
            } else list.add(0, c);
            list2.add(name);
            gameControllers.get(gameIndex).setPlayerFromDisk(name, c);
            if (gameControllers.get(gameIndex).getPlayersNumber() == gameList.get(gameIndex).size())
                gameControllers.get(gameIndex).restartFromDisk(gameList.get(gameIndex));
        }
    }


    public Server() {
    }/* kept for test compatibility */

    public Server(int port, String ip, short moveTimer, TimeUnit moveTimerTimeUnit, String MOTD) {
        try {
            Server.moveTimer = moveTimer;
            Server.moveTimerTimeUnit = moveTimerTimeUnit;
            Server.serverView = (logger.isDebugEnabled() ? new ServerView() : null);
            if (MOTD != null) Server.MOTD = MOTD;
            this.serverSocket = new ServerSocket(port, 1, InetAddress.getByName(ip));
        } catch (Exception e) {
            logger.error("Invalid IP address supplied. Please use a valid ipv4/ipv6 address.", e);
            System.exit(0);
        }
    }

    public void run() {
        // add shutdown hook to notice clients of server death
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            int count = 0;
            logger.info("Caught SIGHUP. Trying to cleanly close connections before dying.");
            while (count < currentGameIndex) {
                try {
                    for (SocketClientConnection c : gameList.get(count)) {
                        c.send(ServerMessage.serverDown);
                    }
                } catch (NullPointerException ignored) {
                } finally {
                    count += 1;
                }
            }
        }));
        //reload games from save files if persistence enabled
        if (ServerMain.persistence()) reloadFromDisk();
        // log some infos
        logger.info("Server started!");
        logger.info("IP: " + serverSocket.getInetAddress() + ", Port: " + serverSocket.getLocalPort());
        logger.info("PID: " + ProcessHandle.current().pid());
        while (true) {
            try {
                Socket newSocket = serverSocket.accept();
                SocketClientConnection socketConnection = new SocketClientConnection(newSocket, this);
                executor.submit(socketConnection);
                logger.debug("Accepted new connection from {}", newSocket.getInetAddress());
            } catch (IOException e) {
                logger.trace("Error in server main thread.", e);
                System.exit(-1);
            }
        }
    }

    synchronized private void reloadFromDisk() {
        FileInputStream fileInputStream;
        for (int i = 0; ; i++) {
            try {
                fileInputStream = new FileInputStream("game" + i + ".save");
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                GameTable g = (GameTable) objectInputStream.readObject();
                if (g.getPlayers() != null && g.getPlayers().size() > ServerMain.getMaxPlayersNumber()) {
                    logger.error("Cannot reload game from disk as new maximum number of players is lower than the saved game's. Please increase the number and try again. ");
                    System.exit(-1);
                }
                MainController mainController = new MainController(g);
                gameControllers.put(i, mainController);
                gameList.put(i, null);
                gameProperties.put(i, null);
            } catch (IOException | ClassNotFoundException e) {
                currentGameIndex = i;
                break;
            }
        }
        logger.info("Successfully reloaded {} games from disk.", currentGameIndex);
    }

    /**
     * Simple functions Server side for developers
     */
    public void startConsole() {
        new Thread(() -> {
            Scanner stdin = new Scanner(System.in);
            logger.info("Console started!");
            while (true) {
                try {
                    String s = stdin.nextLine();
                    if (s.contains("kick")) {
                        String[] input = s.split(" ");
                        MainController controller = gameControllers.get(Integer.parseInt(input[1]));
                        if (controller != null) {
                            controller.consoleKickPlayer(input[2]);
                        }
                    } else if (s.contains("players")) {
                        String[] input = s.split(" ");
                        StringBuilder s1 = new StringBuilder();
                        if (input.length == 1) {
                            for (int i = 0; i < currentGameIndex; i++) {
                                s1.append("Players of game ").append(i).append(": ");
                                try {
                                    gameList.get(i).stream().map(c -> c.getPlayer().getNickname()).forEach(s2 -> {
                                        s1.append(s2);
                                        s1.append(" ");
                                    });
                                    s1.append("\n");
                                } catch (NullPointerException e) {
                                    s1.append("empty\n");
                                }

                            }
                        } else {
                            int index = Integer.parseInt(input[1]);
                            s1.append("Players of game ").append(index).append(": ");
                            try {
                                gameList.get(index).stream().map(c -> c.getPlayer().getNickname()).forEach(s2 -> {
                                    s1.append(s2);
                                    s1.append(" ");
                                });
                            } catch (NullPointerException e) {
                                s1.append("empty");
                            }
                        }
                        System.out.println(s1);
                    } else if (s.contains("save")) {
                        if (ServerMain.persistence())
                            logger.info("Saving state to disk...");
                        else
                            logger.warn("Saving state, although current instance was set not to use persistence.");
                        gameControllers.forEach((key, value) -> value.saveGameState());
                        logger.info("State saved successfully!");
                    } else if (s.contains("motd") || s.contains("MOTD")) {
                        String[] input = s.split(" ");
                        if (input.length > 1) {
                            StringBuilder temp = new StringBuilder();
                            boolean first = true;
                            for (String p : input) {
                                if (first) first = false;
                                else temp.append(p).append(" ");
                            }
                            MOTD = temp.toString();
                        } else System.out.println("MOTD: " + MOTD);
                    } else if (s.equalsIgnoreCase("close")) break;
                    else if (s.equalsIgnoreCase("kill")) System.exit(0);
                    else throw new Exception();
                    logger.debug(Color.ANSI_GREEN + "Console command executed." + Color.RESET);
                } catch (Exception e) {
                    logger.error(Color.ANSI_RED + "Invalid command." + Color.RESET);
                }
            }
        }).start();
    }

}
