package it.polimi.ingsw.Network;

import it.polimi.ingsw.Controller.MainController;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.Player;
import it.polimi.ingsw.Network.Messages.GameStateMessage;
import it.polimi.ingsw.Network.Messages.InitMessage;
import it.polimi.ingsw.Network.Messages.ServerMessage;
import it.polimi.ingsw.ServerMain;
import it.polimi.ingsw.Utility.Pair;
import it.polimi.ingsw.View.RemoteView;
import it.polimi.ingsw.View.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Responsible for initiating a match; gets called asynchronously by {@link it.polimi.ingsw.Network.Server}.
 * Takes care of:
 * - getting players' choices for gods and positions
 * - initializing GameTable
 * - initializing Observer/Observable relations
 * - other magic
 */
@SuppressWarnings("VariableNotUsedInsideIf")
public class GameInitializer implements Runnable {

    private final SocketClientConnection c1, c2, c3;
    private final ArrayList<Integer> gods;
    private final Player player1, player2, player3;
    private final GameTable gameTable;
    private final MainController mainController;
    private final Logger logger = LoggerFactory.getLogger(GameInitializer.class);

    GameInitializer(SocketClientConnection c1, SocketClientConnection c2, SocketClientConnection c3, ArrayList<Integer> gods, Player player1, Player player2, Player player3, GameTable gameTable, MainController mainController) {
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
     *
     * @return list of chosen gods, in order from first to last player
     */
    private ArrayList<Integer> getPlayerGodChoices() {
        if (c3 == null) {
            ArrayList<Integer> out = new ArrayList<>();
            int p2choice = getGodChoice(c2, gods);
            out.add(p2choice);
            c1.send(ServerMessage.lastGod + (gods.get(0)));
            out.add(0, gods.get(0));
            return out;
        } else {
            ArrayList<Integer> out = new ArrayList<>();
            int p2choice = getGodChoice(c2, gods);
            out.add(p2choice);
            c3.send("Here are the available gods:\n");
            int p3choice = getGodChoice(c3, gods);
            out.add(p3choice);
            c1.send(ServerMessage.lastGod + (gods.get(0)));
            out.add(0, gods.get(0));
            return out;
        }
    }

    /**
     * Asks for god choice
     *
     * @param connection connection to send to
     * @param gods       list of previously choosen by the first player
     * @return integer index of the choosen god
     */
    synchronized int getGodChoice(SocketClientConnection connection, ArrayList<Integer> gods) {
        StringBuilder s = new StringBuilder();
        s.append("[CHOICE]@@@");
        for (Integer god : gods) {
            s.append(god).append("@@@");
        }
        connection.send(s.toString());
        Scanner in = null;
        try {
            in = connection.getScanner();
        } catch (IOException e) {
            logger.error("Error during god choice.", e);
        }
        assert in != null;
        int choice = -1;

        while (true) {
            try {
                choice = in.nextInt();
                choice -= 1;
                if (choice < GameTable.completeGodList.size() && choice >= 0 && gods.contains(choice)) {
                    gods.remove(Integer.valueOf(choice));
                    connection.send("You choose: " + GameTable.getCompleteGodList().get(choice));
                    return choice;
                } else {
                    connection.send("Wrong number. Try again: ");
                }
            } catch (InputMismatchException e) {
                connection.send("Wrong number. Try again: ");
                in.nextLine();
            }
        }

    }

    /**
     * Asks for builder coordinates
     *
     * @param connection connection to send to
     * @param choices    list of previously chosen coordinates
     * @return pair of chosen coordinates
     */
    synchronized Pair getBuilderChoice(SocketClientConnection connection, ArrayList<Pair> choices) {
        Pair out;
        int c, r; //sia righe che colonne vanno da 0 a 4 compresi
        Scanner in = null;
        try {
            in = connection.getScanner();
        } catch (IOException e) {
            logger.error("Error during builder choice.", e);
        }
        while (true) {
            assert in != null;
            StringBuilder aa = new StringBuilder();
            aa.append("[CHOICE]@@@POS");
            for (Pair p : choices) {
                aa.append("@@@");
                aa.append(p.getFirst());
                aa.append(",");
                aa.append(p.getSecond());
            }
            connection.send(aa.toString());
            while (true) {
                try {
                    String s = in.nextLine();
                    String[] inputs = s.split(",");
                    if (inputs.length != 2) throw new InputMismatchException();
                    r = Integer.parseInt(inputs[0]);
                    c = Integer.parseInt(inputs[1]);
                    if (r >= 0 && r < 5) {
                        if (c >= 0 && c < 5) break;
                        else connection.send(ServerMessage.wrongNumber);
                    } else {
                        connection.send(ServerMessage.wrongNumber);
                    }
                } catch (InputMismatchException | NumberFormatException e) {
                    connection.send(ServerMessage.wrongNumber);
                }
            }
            out = new Pair(r, c);
            if (!choices.contains(out)) {
                break;
            } else {
                connection.send(ServerMessage.cellNotAvailable);
            }
        }
        return out;
    }

    /**
     * Gets players' initial builder positioning choices
     *
     * @return list of chosen coordinates where to put builders
     */
    private synchronized ArrayList<Pair> getPlayerBuilderChoices() { //metodo che richiede le posizioni iniziali dei worker
        if (c3 != null) {
            c1.send("Wait for the other players to choose their starting position...");
        } else {
            c1.send("Wait for the other player to choose his starting position...");
        }
        ArrayList<Pair> choices = new ArrayList<Pair>(); //array che conterrà tutte le coppie delle posizioni iniziali
        c2.send(ServerMessage.firstBuilderPos);
        Pair c2b1 = getBuilderChoice(c2, choices); //nomenclatura è NomeConnessione+NumeroWorker
        choices.add(c2b1); //aggiungo man mano ogni coppia all'array choices. Il controllo dell'input avviene nel metodo getBuilderChoice.
        c2.send(ServerMessage.secondBuilderPos);
        Pair c2b2 = getBuilderChoice(c2, choices);
        choices.add(c2b2);
        if (c3 != null){
            c2.send("Wait for the other players to choose their starting position...");
            c3.send(ServerMessage.firstBuilderPos);
            Pair c3b1 = getBuilderChoice(c3, choices);
            choices.add(c3b1);
            c3.send(ServerMessage.secondBuilderPos);
            Pair c3b2 = getBuilderChoice(c3, choices);
            choices.add(c3b2);
            c3.send("Wait for the other player to choose his starting positions...");
        }
        else {
            c2.send("Wait for the other player to choose his starting positions...");
        }
        c1.send(ServerMessage.firstBuilderPos);
        Pair c1b1 = getBuilderChoice(c1, choices);
        choices.add(0, c1b1);
        c1.send(ServerMessage.secondBuilderPos);
        Pair c1b2 = getBuilderChoice(c1, choices);
        choices.add(1,c1b2);
        return choices;
    }


    @Override
    public void run() {
        try {
            ArrayList<Integer> choices = getPlayerGodChoices();
            player1.setGod(choices.get(0));
            player2.setGod(choices.get(1));
            if (c3 != null) player3.setGod(choices.get(2));
            ArrayList<Pair> startPos = getPlayerBuilderChoices();
            try {
                player1.initBuilderList(gameTable.getCell(startPos.get(0).getFirst(), startPos.get(0).getSecond()));
                player1.initBuilderList(gameTable.getCell(startPos.get(1).getFirst(), startPos.get(1).getSecond()));
                player2.initBuilderList(gameTable.getCell(startPos.get(2).getFirst(), startPos.get(2).getSecond()));
                player2.initBuilderList(gameTable.getCell(startPos.get(3).getFirst(), startPos.get(3).getSecond()));
                if (c3 != null) {
                    player3.initBuilderList(gameTable.getCell(startPos.get(4).getFirst(), startPos.get(4).getSecond()));
                    player3.initBuilderList(gameTable.getCell(startPos.get(5).getFirst(), startPos.get(5).getSecond()));
                }
            } catch (InvalidBuildException | InvalidCoordinateException e) {
                logger.error("Error during builders initialization.", e);
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
            gameTable.addPropertyChangeListener(player1View);
            gameTable.addPropertyChangeListener(player2View);
            if (c3 != null) {
                gameTable.addPropertyChangeListener(player3View);
            }

            player1View.addPropertyChangeListener(mainController);
            player2View.addPropertyChangeListener(mainController);
            if (logger.isDebugEnabled()) {
                gameTable.addPropertyChangeListener(Server.getServerView());
            }
            if (c3 != null) {
                player3View.addPropertyChangeListener(mainController);
            }

            c1.setReady();
            c2.setReady();
            if (c3 != null) {
                c3.setReady();
            }
            c1.send(new InitMessage(gameTable.getPlayerIndex(player1), players.size(), gameTable.getGameIndex(), GameTable.completeGodList.indexOf(c1.getPlayer().getGod()), GameTable.completeGodList.indexOf(c2.getPlayer().getGod()), c3 != null ? GameTable.completeGodList.indexOf(c3.getPlayer().getGod()) : -1));
            c2.send(new InitMessage(gameTable.getPlayerIndex(player2), players.size(), gameTable.getGameIndex(), GameTable.completeGodList.indexOf(c1.getPlayer().getGod()), GameTable.completeGodList.indexOf(c2.getPlayer().getGod()), c3 != null ? GameTable.completeGodList.indexOf(c3.getPlayer().getGod()) : -1));

            if (c3 != null) {
                c3.send(new InitMessage(gameTable.getPlayerIndex(player3), players.size(), gameTable.getGameIndex(), GameTable.completeGodList.indexOf(c1.getPlayer().getGod()), GameTable.completeGodList.indexOf(c2.getPlayer().getGod()), GameTable.completeGodList.indexOf(c3.getPlayer().getGod())));
            }
            GameStateMessage message = gameTable.getGameState();
            c1.send(message);
            c2.send(message);
            if (c3 != null) c3.send(message);
            c1.send(gameTable.getBoardCopy());
            c2.send(gameTable.getBoardCopy());
            if (c3 != null) c3.send(gameTable.getBoardCopy());
            gameTable.resetMoveTimer();
            if (ServerMain.persistence()) mainController.saveGameState();
        }
        catch (Exception e){
            c1.closeConnection();
            c2.closeConnection();
            if (c3 != null) c3.closeConnection();
        }

    }
}
