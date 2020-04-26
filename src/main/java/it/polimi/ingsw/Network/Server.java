package it.polimi.ingsw.Network;

import it.polimi.ingsw.Controller.MainController;
import it.polimi.ingsw.Model.Exceptions.InvalidGodException;
import it.polimi.ingsw.Model.Exceptions.NickAlreadyTakenException;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.Pair;
import it.polimi.ingsw.Model.Player;
import it.polimi.ingsw.View.RemoteView;
import it.polimi.ingsw.View.View;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Server {
    private int k;
    private static final int PORT = 1337;
    private final ServerSocket serverSocket;
    private final ExecutorService executor = Executors.newFixedThreadPool(128); /** Thread pool creator **/
    private final Map<String, SocketClientConnection> waitingConnection = new LinkedHashMap<>(); /** contains player connections waiting to be matchmade **/
    private final Map<Integer, ArrayList<SocketClientConnection>> gameList = new HashMap<>(); /** Integer contains the game index, ArrayList contains client connections for the respective game **/
    private final Map<Integer, ArrayList<Integer>> gameProperties = new HashMap<>(); /** position [0] contains the number of players for the game; positions [1..2/3] contain the chosen gods**/
    private static int currentGameIndex = 0; /** index of game currently in the process of being created; eg: if it's set to 1 it means game 0 is already started / finished, while game 1 is being made **/

    public synchronized static int getCurrentGameIndex() {
        return currentGameIndex;
    }

    //Deregister connection
  /*  public synchronized void deregisterConnection(SocketClientConnection c) {
        SocketClientConnection opponent = playingConnection.get(c);
        if(opponent != null) {
            opponent.closeConnection();
        }
        playingConnection.remove(c);
        playingConnection.remove(opponent);
        waitingConnection.keySet().removeIf(s -> waitingConnection.get(s) == c);
    }*/

    //Wait for another player
    public synchronized void lobby(SocketClientConnection c, String name) throws InvalidGodException, NickAlreadyTakenException {
        if (waitingConnection.containsKey(name)) throw new NickAlreadyTakenException();
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
            try{
                List<String> keys = new ArrayList<>(waitingConnection.keySet());
                SocketClientConnection c1 = waitingConnection.get(keys.get(0));
                SocketClientConnection c2 = waitingConnection.get(keys.get(1));
                SocketClientConnection c3 = null;
                if (waitingConnection.size() == 3) c3 = waitingConnection.get(keys.get(2));
                ArrayList<Integer> gods = new ArrayList<>();
                for (int i = 1; i <= gameProperties.get(getCurrentGameIndex()).get(0); i++){
                    gods.add(gameProperties.get(getCurrentGameIndex()).get(i));
                }
                ArrayList<Integer> choices = getPlayerGodChoices(c1,c2,c3,gods);

                GameTable gameTable = new GameTable(keys.size(),choices);

                Player player1 = new Player(keys.get(0), choices.get(0), gameTable);
                Player player2 = new Player(keys.get(1), choices.get(1), gameTable);
                Player player3 = null;

                if (c3 != null) {
                    player3 = new Player(keys.get(2), choices.get(2), gameTable);
                }
                View player1View = new RemoteView(player1, c1);
                View player2View = new RemoteView(player2, c2);
                View player3View = null;
                if (c3 != null) {
                    player3View = new RemoteView(player3, c3);
                }
                ArrayList<Player> players = new ArrayList<>();
                players.add(player1); players.add(player2);
                if (player3 != null) players.add(player3);
                gameTable.setPlayers(players);
                MainController controller = new MainController(keys.size());
                gameTable.addPropertyChangeListener(player1View);
                gameTable.addPropertyChangeListener(player2View);
                if (c3 != null){
                    gameTable.addPropertyChangeListener(player3View);
                }
                player1View.addPropertyChangeListener(controller);
                player2View.addPropertyChangeListener(controller);
                if (c3 != null){
                    player3View.addPropertyChangeListener(controller);
                }
                ArrayList<SocketClientConnection> playingConnections = new ArrayList<>();
                playingConnections.add(c1);
                playingConnections.add(c2);
                if (c3 != null) playingConnections.add(c3);
                gameList.put(getCurrentGameIndex(), playingConnections);
                waitingConnection.clear();

                c1.asyncSend(gameTable.getBoardCopy());
                c2.asyncSend(gameTable.getBoardCopy());
                if (c3 != null) c3.asyncSend(gameTable.getBoardCopy());
              /*  c1.asyncSend(gameMessage.moveMessage);
                c2.asyncSend(gameMessage.waitMessage);
                if (c3 != null) c3.asyncSend(gameMessage.waitMessage);*/
                c1.asyncSend("asd");
                c2.asyncSend("asdasd");
                if (c3 != null) c3.asyncSend("asdasdasd");

            }  finally {
                currentGameIndex += 1;
            }
        }
    }

    private synchronized ArrayList<Integer> getPlayerGodChoices(SocketClientConnection c1, SocketClientConnection c2, SocketClientConnection c3, ArrayList<Integer> gods){
        if(c3 == null) {
            c1.asyncSend("Sei stato il primo a connetterti");
            c2.asyncSend("Sei stato il secondo a connetterti");
            c2.send("Here are the available gods:\n");
            ArrayList<Integer> out = new ArrayList<>();
            int p2choice = c2.getGodChoice(gods);
            out.add(p2choice);
            c1.send("You're left with " + GameTable.getCompleteGodList().get(gods.get(0)));
            out.add(0,gods.get(0));
            return out;
        }
        else {
            c1.asyncSend("Sei stato il primo a connetterti");
            c2.asyncSend("Sei stato il secondo a connetterti");
            c3.asyncSend("Sei stato il terzo a connetterti");
            c2.send("Here are the available gods:\n");
            ArrayList<Integer> out = new ArrayList<>();
            int p2choice = c2.getGodChoice(gods);
            out.add(p2choice);
            c3.send("Here are the available gods:\n");
            for (Integer god : gods) {
                c3.send(god + ") " + GameTable.getCompleteGodList().get(god) + "\n");
            }
            int p3choice = c3.getGodChoice(gods);
            out.add(p3choice);
            c1.send("You're left with " + GameTable.getCompleteGodList().get(gods.get(0)));
            out.add(0,gods.get(0));
            return out;
        }
    }

    public Server() throws IOException {
        this.serverSocket = new ServerSocket(PORT);
    }

    public void run(){
        //noinspection InfiniteLoopStatement
        while(true){
            try {
                Socket newSocket = serverSocket.accept();
                SocketClientConnection socketConnection = new SocketClientConnection(newSocket, this);
                executor.submit(socketConnection);
            } catch (IOException e) {
                System.out.println("Connection Error!");
            }
        }
    }

}
