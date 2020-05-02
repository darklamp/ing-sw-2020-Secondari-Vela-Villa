package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.Pair;
import it.polimi.ingsw.Model.Player;
import it.polimi.ingsw.Network.SocketClientConnection;
import it.polimi.ingsw.View.RemoteView;
import it.polimi.ingsw.View.View;

import java.util.ArrayList;

/**
 * Responsible for initiating a match; gets called asynchronously by {@link it.polimi.ingsw.Network.Server}
 */
public class InitController implements Runnable{

    private final SocketClientConnection c1,c2,c3;
    private final ArrayList<Integer> gods;
    private final Player player1,player2,player3;
    private final GameTable gameTable;
    private final MainController mainController;

    public InitController(SocketClientConnection c1, SocketClientConnection c2, SocketClientConnection c3, ArrayList<Integer> gods, Player player1, Player player2, Player player3, GameTable gameTable, MainController mainController){
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
        this.gods = gods;
        this.player1 = player1;
        this.player2 = player2;
        this.player3 = player3;
        this.gameTable = gameTable;
        this.mainController = mainController;
    }

    /**
     * Gets players' god choices
     * @param c1,c2,c3 client connections
     * @param gods list of available gods, from which players have to choose
     * @return list of chosen gods, in order from first to last player
     */
    /*synchronized*/ public ArrayList<Integer> getPlayerGodChoices(SocketClientConnection c1, SocketClientConnection c2, SocketClientConnection c3, ArrayList<Integer> gods){
        if(c3 == null) {
            c2.send("Here are the available gods:\n");
            ArrayList<Integer> out = new ArrayList<>();
            int p2choice = c2.getGodChoice(gods);
            out.add(p2choice);
            c1.send("You're left with " + GameTable.getCompleteGodList().get(gods.get(0)));
            out.add(0,gods.get(0));
            return out;
        }
        else {
            c2.send("Here are the available gods:\n");
            ArrayList<Integer> out = new ArrayList<>();
            int p2choice = c2.getGodChoice(gods);
            out.add(p2choice);
            c3.send("Here are the available gods:\n");
            int p3choice = c3.getGodChoice(gods);
            out.add(p3choice);
            c1.send("You're left with " + GameTable.getCompleteGodList().get(gods.get(0)));
            out.add(0,gods.get(0));
            return out;
        }
    }

    /**
     * Gets players' initial builder positioning choices
     * @param c1,c2,c3 client connections
     * @return list of chosen coordinates where to put builders
     */
    private synchronized ArrayList<Pair> getPlayerBuilderChoices(SocketClientConnection c1, SocketClientConnection c2, SocketClientConnection c3) { //metodo che richiede le posizioni inziali dei worker
        if (c3 != null) {
            c1.send("Wait for the other players to choose their starting position...");
        }
        else {
            c1.send("Wait for the other player to choose his starting position...");
        }
        ArrayList<Pair> choices = new ArrayList<Pair>(); //array che conterrà tutte le coppie delle posizioni iniziali
        c2.send("Insert the starting positions of your first worker");
        Pair c2b1 = c2.getBuilderChoice(choices); //nomenclatura è NomeConnessione+NumeroWorker
       // System.out.println("Il giocatore 1 per il lavoratore 1 ha inserito riga "+c2b1.getFirst()+" e colonna "+c2b1.getSecond());
        choices.add(c2b1); //aggiungo man mano ogni coppia all'array choices. Il controllo dell'input avviene nel metodo getBuilderChoice.
        c2.send("Insert the starting positions of your second worker");
        Pair c2b2 = c2.getBuilderChoice(choices);
        choices.add(c2b2);
        if (c3 != null){
            c2.send("Wait for the other players to choose their starting position...");
            c3.send("Insert the starting positions of your first worker");
            Pair c3b1 = c3.getBuilderChoice(choices);
            choices.add(c3b1);
            c3.send("Insert the starting positions of your second worker");
            Pair c3b2 = c3.getBuilderChoice(choices);
            choices.add(c3b2);
            c3.send("Wait for the other player to choose his starting positions...");
        }
        else {
            c2.send("Wait for the other player to choose his starting positions...");
        }
        c1.send("Insert the starting positions of your first worker");
        Pair c1b1 = c1.getBuilderChoice(choices);
        choices.add(0,c1b1);
        c1.send("Insert the starting positions of your second worker");
        Pair c1b2 = c1.getBuilderChoice(choices);
        choices.add(1,c1b2);
        return choices;
    }


    @Override
    public void run() {
        ArrayList<Integer> choices = getPlayerGodChoices(c1,c2,c3,gods);
        player1.setGod(choices.get(0));
        player2.setGod(choices.get(1));
        if(c3 != null) player1.setGod(choices.get(2));
        ArrayList<Pair> startPos = getPlayerBuilderChoices(c1,c2,c3);
        try {
            player1.initBuilderList(gameTable.getCell(startPos.get(0).getFirst(), startPos.get(0).getSecond()));
            player1.initBuilderList(gameTable.getCell(startPos.get(1).getFirst(), startPos.get(1).getSecond()));
            player2.initBuilderList(gameTable.getCell(startPos.get(2).getFirst(), startPos.get(2).getSecond()));
            player2.initBuilderList(gameTable.getCell(startPos.get(3).getFirst(), startPos.get(3).getSecond()));
            if (player3 != null) {
                player3.initBuilderList(gameTable.getCell(startPos.get(4).getFirst(), startPos.get(4).getSecond()));
                player3.initBuilderList(gameTable.getCell(startPos.get(5).getFirst(), startPos.get(5).getSecond()));
            }
        } catch (InvalidBuildException | InvalidCoordinateException e) {
            e.printStackTrace();
        }
        View player1View = new RemoteView(player1, c1, gameTable);
        View player2View = new RemoteView(player2, c2, gameTable);
        View player3View = null;
        if (c3 != null) {
            player3View = new RemoteView(player3, c3, gameTable);
        }
        ArrayList<Player> players = new ArrayList<>();
        players.add(player1); players.add(player2);
        if (player3 != null) players.add(player3);
        gameTable.setPlayers(players);
        gameTable.setGods(choices);
        gameTable.addPropertyChangeListener(player1View);
        gameTable.addPropertyChangeListener(player2View);
        if (c3 != null){
            gameTable.addPropertyChangeListener(player3View);
        }
        player1View.addPropertyChangeListener(mainController);
        player2View.addPropertyChangeListener(mainController);
        if (c3 != null){
            player3View.addPropertyChangeListener(mainController);
        }
        c1.setReady(); c2.setReady();
        if (c3 != null){
            c3.setReady();
        }
       c1.asyncSend(gameTable.getBoardCopy());
       c2.asyncSend(gameTable.getBoardCopy());
       if (c3 != null) c3.asyncSend(gameTable.getBoardCopy());
       c2.setState(ClientState.MOVE);
    }
}
