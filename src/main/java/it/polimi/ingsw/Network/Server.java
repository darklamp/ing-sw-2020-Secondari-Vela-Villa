package it.polimi.ingsw.Network;

import it.polimi.ingsw.Controller.MainController;
import it.polimi.ingsw.Model.Exceptions.NickAlreadyTakenException;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.Player;
import it.polimi.ingsw.Network.Exceptions.BrokenLobbyException;
import it.polimi.ingsw.ServerMain;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    public static short getMoveTimer() {
        return moveTimer;
    }

    /**
     * index of game currently in the process of being created; eg: if it's set to 1 it means game 0 is already started / finished, while game 1 is being made
     **/
    private final static short moveTimer = 2;

    public synchronized static int getCurrentGameIndex() {
        return currentGameIndex;
    }

    /**
     * @param c is the client kicked from server
     */
    public synchronized static void deregisterConnection(SocketClientConnection c) {
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
     * @param c is the client connection
     * @param name is the Nickname
     * @throws Exception, in this case the lobby is reset
     * Handle the interaction with the first player and then wait for the other player(s), once the game
     * is started the lobby is emptied and ready to "host" another game.
     */
    public synchronized void lobby(SocketClientConnection c, String name) throws Exception {
     try{
         if (waitingConnection.containsKey(name) || name.equals("") || name.contains("\n")) throw new NickAlreadyTakenException();
         System.out.println("nome è "+name);
         waitingConnection.put(name, c);
         if (waitingConnection.size() == 1){
             ArrayList<Integer> gameProps = c.firstPlayer();
             ArrayList<SocketClientConnection> temp= new ArrayList<>();
             temp.add(c);
             gameList.put(getCurrentGameIndex(),temp);
             gameProperties.put(getCurrentGameIndex(),gameProps);
         }
         else if (! (waitingConnection.size() == gameProperties.get(getCurrentGameIndex()).get(0))){
             ArrayList<SocketClientConnection> temp =  gameList.get(getCurrentGameIndex());
             temp.add(c);
         }
         else { // start game
                 List<String> keys = new ArrayList<>(waitingConnection.keySet());
                 SocketClientConnection c1 = waitingConnection.get(keys.get(0));
                 SocketClientConnection c2 = waitingConnection.get(keys.get(1));
                 SocketClientConnection c3 = null;
                 if (waitingConnection.size() == 3) c3 = waitingConnection.get(keys.get(2));
                 ArrayList<Integer> gods = new ArrayList<>();
                 for (int i = 1; i <= gameProperties.get(getCurrentGameIndex()).get(0); i++){
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
             Thread thread = new Thread(gameInitializer);
             thread.start();
             gameControllers.put(getCurrentGameIndex(), controller);
             ArrayList<SocketClientConnection> playingConnections = new ArrayList<>();
             playingConnections.add(c1);
             playingConnections.add(c2);
             if (c3 != null) playingConnections.add(c3);
             gameList.put(getCurrentGameIndex(), playingConnections);
             waitingConnection.clear();
             //      thread.join();
             currentGameIndex += 1;
         }
     } catch (NickAlreadyTakenException e) {
         throw new NickAlreadyTakenException();
     } catch (Exception e) {
         waitingConnection.clear();
         if (gameList.size() > 0) gameList.get(getCurrentGameIndex()).clear();
         if (gameProperties.size() > 0) gameProperties.get(getCurrentGameIndex()).clear();
         throw new BrokenLobbyException();
     }

    }

    private static final Map<Integer, ArrayList<String>> gameFromDiskList = new LinkedHashMap<>();

    public synchronized void lobbyFromDisk(SocketClientConnection c, String name, int gameIndex) throws NickAlreadyTakenException {
        if (!gameControllers.get(gameIndex).containsPlayer(name)) throw new NickAlreadyTakenException();
        else {
            ArrayList<SocketClientConnection> list = gameList.computeIfAbsent(gameIndex, k -> new ArrayList<>());
            ArrayList<String> list2 = gameFromDiskList.computeIfAbsent(gameIndex, k -> new ArrayList<>());
            if (list2.size() > 0) {
                if (list2.stream().anyMatch(c1 -> c1.equals(name))) throw new NickAlreadyTakenException();
            }
            list.add(c);
            list2.add(name);
            gameControllers.get(gameIndex).setPlayerFromDisk(name, c);
            if (gameControllers.get(gameIndex).getPlayersNumber() == gameList.get(gameIndex).size())
                gameControllers.get(gameIndex).restartFromDisk(gameList.get(gameIndex));
        }
    }


    public Server() throws IOException {
    }/* kept for test compatibility */

    public Server(int port, String ip) {
        try {
            this.serverSocket = new ServerSocket(port, 1, InetAddress.getByName(ip));
        } catch (Exception e) {
            System.err.println("[CRITICAL] Invalid IP address supplied. Please use a valid ipv4/ipv6 address.");
            System.exit(0);
        }
    }

    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                for (Map.Entry<Integer, ArrayList<SocketClientConnection>> c : gameList.entrySet()) {
                    for (SocketClientConnection socketClientConnection : c.getValue()) {
                        socketClientConnection.send(ServerMessage.serverDown);
                    }
                }
            } catch (NullPointerException ignored) {
            }
        }));
        if (ServerMain.persistence()) reloadFromDisk();
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                Socket newSocket = serverSocket.accept();
                SocketClientConnection socketConnection = new SocketClientConnection(newSocket, this);
                executor.submit(socketConnection);
            } catch (IOException e) {
                System.out.println("Connection Error!");
            }
        }
    }

    synchronized public void reloadFromDisk() {
        FileInputStream fileInputStream;
        for (int i = 0; ; i++) {
            try {
                fileInputStream = new FileInputStream("game" + i);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                GameTable g = (GameTable) objectInputStream.readObject();
                MainController mainController = new MainController(g);
                gameControllers.put(i, mainController);
                gameList.put(i, null);
                gameProperties.put(i, null);
            } catch (IOException | ClassNotFoundException e) {
                currentGameIndex = i;
                break;
            }
        }
        System.out.println("Successfully reloaded " + currentGameIndex + " games from disk.");
    }

    /**
     * Simple functions Server side for developers
     */
    public void startConsole() {
        new Thread(() -> {
            Scanner stdin = new Scanner(System.in);
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
                        int index = Integer.parseInt(input[1]);
                        s1.append("Players of game ").append(index).append(": ");
                        gameList.get(index).iterator().forEachRemaining(c -> s1.append(c.getPlayer().getNickname()).append(" "));
                        System.out.println(s1);
                    } else if (s.contains("save")) {
                        if (ServerMain.persistence())
                            System.out.println("Saving state to disk..");
                        else
                            System.out.println("[WARNING] Saving state, although current instance was set not to use persistence.");
                        gameControllers.forEach((key, value) -> value.saveGameState());
                        System.out.println("State saved successfully!");
                    }
                } catch (Exception e) {
                    if (ServerMain.verbose()) e.printStackTrace();
                }
            }
        }).start();
    }

}
