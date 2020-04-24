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

    private static final int PORT = 1337;
    private final ServerSocket serverSocket;
    private final ExecutorService executor = Executors.newFixedThreadPool(64);
    private Map<String, SocketClientConnection> waitingConnection = new HashMap<>();
    private Map<SocketClientConnection, SocketClientConnection> playingConnection = new HashMap<>();
    private Map<Integer, ArrayList<SocketClientConnection>> gameList = new HashMap<>();
    private Map<Integer, ArrayList<Integer>> gameProperties = new HashMap<>(); /* position [0] contains the number of players for the game; positions [1..2/3] contain the chosen gods*/
    private static int currentGameIndex = 0;

    public synchronized static int getCurrentGameIndex() {
        return currentGameIndex;
    }

    //Deregister connection
    public synchronized void deregisterConnection(SocketClientConnection c) {
        SocketClientConnection opponent = playingConnection.get(c);
        if(opponent != null) {
            opponent.closeConnection();
        }
        playingConnection.remove(c);
        playingConnection.remove(opponent);
        waitingConnection.keySet().removeIf(s -> waitingConnection.get(s) == c);
    }

    //Wait for another player
    public synchronized void lobby(SocketClientConnection c, String name) throws InvalidGodException, NickAlreadyTakenException {
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
                for (int i = 1; i < gameProperties.get(getCurrentGameIndex()).get(0); i++){
                    gods.add(gameProperties.get(getCurrentGameIndex()).get(i));
                }
                ArrayList<Integer> choices = getPlayerGodChoices(c1,c2,c3,gods);

                Player player1 = new Player(keys.get(0), choices.get(0));
                Player player2 = new Player(keys.get(1), choices.get(1));
               // View player1View = new RemoteView(player1, keys.get(1), c1);
                //View player2View = new RemoteView(player2, keys.get(0), c2);
           /*     Model model = new Model();
                MainControllerController controller = new MainController();
                model.addObserver(player1View);
                model.addObserver(player2View);
                player1View.addObserver(controller);
                player2View.addObserver(controller);
                playingConnection.put(c1, c2);
                playingConnection.put(c2, c1);
                waitingConnection.clear();

                c1.asyncSend(model.getBoardCopy());
                c2.asyncSend(model.getBoardCopy());
                if(model.isPlayerTurn(player1)){
                    c1.asyncSend(gameMessage.moveMessage);
                    c2.asyncSend(gameMessage.waitMessage);
                } else {
                    c2.asyncSend(gameMessage.moveMessage);
                    c1.asyncSend(gameMessage.waitMessage);
                }*/
            }  finally {
                currentGameIndex += 1;
            }
        }
      /*  if (playersNumber != 0 && waitingConnection.size() == playersNumber) {
            List<String> keys = new ArrayList<>(waitingConnection.keySet());
            SocketClientConnection c1 = waitingConnection.get(keys.get(0));
            SocketClientConnection c2 = waitingConnection.get(keys.get(1));
            Player player1 = new Player(keys.get(0), Cell.X);
            Player player2 = new Player(keys.get(0), Cell.O);
            View player1View = new RemoteView(player1, keys.get(1), c1);
            View player2View = new RemoteView(player2, keys.get(0), c2);
            Model model = new Model();
            MainControllerController controller = new MainController();
            model.addObserver(player1View);
            model.addObserver(player2View);
            player1View.addObserver(controller);
            player2View.addObserver(controller);
            playingConnection.put(c1, c2);
            playingConnection.put(c2, c1);
            waitingConnection.clear();

            c1.asyncSend(model.getBoardCopy());
            c2.asyncSend(model.getBoardCopy());
            if(model.isPlayerTurn(player1)){
                c1.asyncSend(gameMessage.moveMessage);
                c2.asyncSend(gameMessage.waitMessage);
            } else {
                c2.asyncSend(gameMessage.moveMessage);
                c1.asyncSend(gameMessage.waitMessage);
            }


        }*/
    }

    private synchronized ArrayList<Integer> getPlayerGodChoices(SocketClientConnection c1, SocketClientConnection c2, SocketClientConnection c3, ArrayList<Integer> gods){
        c2.asyncSend("Here are the available gods:\n");
        ArrayList<Integer> out = new ArrayList<>();
        for(int i : gods){
            c2.asyncSend(Integer.toString(i) + ") " + GameTable.getCompleteGodList().get(gods.get(i)) +"\n");
        }
        c2.asyncSend("Please choose one: ");
        int p2choice = c2.getGodChoice(gods);
        out.add(p2choice);
        if (c3 == null){ out.add(gods.get(0) == p2choice ? gods.get(1) : gods.get(0)); return out;}
        else {
            int p3choice = c3.getGodChoice(gods);
            out.add(gods.get(0) == p3choice ? gods.get(1) : gods.get(0)); return out;
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
