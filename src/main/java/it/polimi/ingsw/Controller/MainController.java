package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Controller.Exceptions.IllegalTurnStateException;
import it.polimi.ingsw.Model.Exceptions.NoMoreMovesException;
import it.polimi.ingsw.Model.Exceptions.WinnerException;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.News;
import it.polimi.ingsw.Model.Player;
import it.polimi.ingsw.Network.GameInitializer;
import it.polimi.ingsw.Network.Messages.GameStateMessage;
import it.polimi.ingsw.Network.Messages.InitMessage;
import it.polimi.ingsw.Network.Server;
import it.polimi.ingsw.Network.SocketClientConnection;
import it.polimi.ingsw.ServerMain;
import it.polimi.ingsw.View.RemoteView;
import it.polimi.ingsw.View.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.ArrayList;

/**
 * Manage the news received.
 */

public class MainController implements PropertyChangeListener {

    private Player currentPlayer;
    private final BuildController buildController;
    private final MoveController moveController;
    private final GameTable gameTable;
    private GameInitializer gameInitializer;
    private final Logger logger = LoggerFactory.getLogger(MainController.class);
    /**
     * If persistence is enabled, this attribute contains a reference to file used to save the game's state.
     */
    private FileOutputStream fileOutputStream;
    /**
     * News object coming in from {@link RemoteView}'s MessageReceiver
     */
    private News news;

    /**
     * Handles news objects received by remote views. Equivalent of {@link java.util.Observable}'s update().
     *
     * @see PropertyChangeListener
     */
    @Override
    synchronized public void propertyChange(PropertyChangeEvent propertyChangeEvent) { // equivalente di update
        Object obj = propertyChangeEvent.getNewValue();
        String name = propertyChangeEvent.getPropertyName();
        news = (News) obj;
        if (!news.isValid()) {
            news.setRecipients(news.getSender().getPlayer());
            gameTable.setNews(news, "INVALIDNEWS");
            return;
        }
        try {
            currentPlayer = gameTable.getCurrentPlayer();
        } catch (IndexOutOfBoundsException e) {
            return;
        }
        if (name.equals("ABORT")) {
            News n = new News(null, gameTable.getPlayerConnections().get(0));
            gameTable.setNews(n, "ABORT");
            gameTable.closeGame();
        } else if (currentPlayer != news.getSender().getPlayer()) {
            news.setRecipients(news.getSender().getPlayer());
            gameTable.setNews(news, "NOTYOURTURN");
        } else {
            try {
                if (name.equals("PLAYERTIMEOUT")) throw new NoMoreMovesException(news.getSender().getPlayer());
                else {
                    isLegalTurnState(name, currentPlayer.getState());
                    switch (name) {
                        case "PASS" -> {
                            gameTable.nextTurn();
                            currentPlayer = gameTable.getCurrentPlayer();
                        }
                        case "BUILD" -> buildController.handleBuild(news);
                        case "MOVE" -> moveController.handleMove(news);
                        /* note : the "default" case should never be met, since the isLegalTurnState function has already filtered the news out */
                        default -> throw new IllegalTurnStateException();
                    }
                    if (ServerMain.persistence()) saveGameState();
                }
            } catch (IllegalTurnStateException e) { // player isn't in a legal state for the move
                news.setRecipients(gameTable.getCurrentPlayer());
                gameTable.setNews(news, "ILLEGALSTATE");
            } catch (WinnerException e) {
                SocketClientConnection winner = gameTable.getPlayerConnections().get(0);
                for (SocketClientConnection c : gameTable.getPlayerConnections()) {
                    c.getPlayer().setState(ClientState.LOSE);
                    if (c.getPlayer() == e.getPlayer()) {
                        winner = c;
                    }
                }
                winner.getPlayer().setState(ClientState.WIN);
                News n = new News(null, winner);
                n.setRecipients((ArrayList<SocketClientConnection>) null);
                gameTable.setNews(n, "WIN");
                gameTable.closeGame();
            } catch (NoMoreMovesException e) {
                if (gameTable.getPlayerConnections().size() == 2) {
                    endGame(e);
                } else {
                    try {
                        gameTable.removePlayer(e.getPlayer(), true);
                    } catch (NoMoreMovesException ee) {
                        endGame(ee);
                    }
                }
            }
        }

    }

    /**
     * Called when the game has to be closed. Takes care of clearing gametable and
     * setting the winner/losers.
     *
     * @param e exception containing the player who has no more moves
     */
    private void endGame(NoMoreMovesException e) {
        SocketClientConnection winner = gameTable.getPlayerConnections().get(0);
        Player loser = e.getPlayer();
        for (SocketClientConnection c : gameTable.getPlayerConnections()) {
            if (c.getPlayer() != e.getPlayer()) {
                winner = c;
                break;
            }
        }
        News n = new News(null, winner);
        n.setRecipients((ArrayList<SocketClientConnection>) null);
        winner.getPlayer().setState(ClientState.WIN);
        loser.setState(ClientState.LOSE);
        gameTable.setNews(n, "WIN");
        gameTable.closeGame();
    }

    public News getNews() {
        return this.news;
    }

    /**
     * Checks whether the player is in a legal state for the move it wants to make.
     *
     * @param name Turn state from Client side
     * @param turn Turn state from Server side
     * @throws IllegalTurnStateException if not matching
     */
    public static void isLegalTurnState(String name, ClientState turn) throws IllegalTurnStateException {
        switch (turn) {
            case BUILD -> {
                if (!name.equals("BUILD")) throw new IllegalTurnStateException();
            }
            case MOVE -> {
                if (!name.equals("MOVE")) throw new IllegalTurnStateException();
            }
            case MOVEORBUILD -> {
                if (!name.equals("MOVE") && !name.equals("BUILD")) throw new IllegalTurnStateException();
            }
            case BUILDORPASS -> {
                if (!name.equals("PASS") && !name.equals("BUILD")) throw new IllegalTurnStateException();
            }
            default -> throw new IllegalTurnStateException();
        }
    }

    /**
     * Constructor for the MainController.Takes gameTable as argument.
     * @param gameTable table of the relative game.
     */
    public MainController(GameTable gameTable) {
        this.currentPlayer = null;
        this.news = null;
        if (ServerMain.persistence()) {
            File file = new File("game" + gameTable.getGameIndex() + ".save");
            try {
                this.fileOutputStream = new FileOutputStream(file);
            } catch (FileNotFoundException ignored) {
            }
        }
        this.gameTable = gameTable;
        this.buildController = new BuildController(gameTable);
        this.moveController = new MoveController(gameTable);
        if (ServerMain.persistence()) saveGameState();
    }

    public void setGameInitializer(GameInitializer gameInitializer) {
        if (this.gameInitializer == null) this.gameInitializer = gameInitializer;
    }

    /**
     * If present, kicks the player from the game and checks whether other players possibly won as a result of that
     * @param playerNick player to be kicked from game
     */
    public void consoleKickPlayer(String playerNick) {
        gameTable.getPlayers().stream().filter(c -> c.getNickname().equals(playerNick)).findFirst().ifPresent(player -> {
            try {
                gameTable.removePlayer(player, true);
            } catch (Exception ignored) {
            }
        });
    }

    /**
     * Persistence method to save the current game's state to disk.
     */
    synchronized public void saveGameState() {
        ObjectOutputStream outputStream;
        try {
            try {
                outputStream = new ObjectOutputStream(fileOutputStream);
            } catch (IOException | NullPointerException e) {
                fileOutputStream = new FileOutputStream("game" + gameTable.getGameIndex() + ".save");
                outputStream = new ObjectOutputStream(fileOutputStream);
            }
            outputStream.writeObject(gameTable);
            outputStream.close();
        } catch (Exception e) {
            logger.error("Error during game save to file.", e);
        }
    }

    /**
     * Checks whether a player is present in the game, by nickname
     *
     * @param playerNick Nickname of players to be checked
     * @return index of player if present, else -1
     */
    synchronized public int containsPlayer(String playerNick) {
        for (Player p : gameTable.getPlayers()) {
            if (p.getNickname().equals(playerNick)) return gameTable.getPlayerIndex(p);
        }
        return -1;
    }

    /**
     * Persistence method to recreate player from disk.
     * @param name name of players to be set
     * @param c    connection to be associated with the player
     */
    synchronized public void setPlayerFromDisk(String name, SocketClientConnection c) {
        Player p = null;
        for (Player p1 : gameTable.getPlayers()) if (p1.getNickname().equals(name)) p = p1;
        assert p != null;
        p.setConnection(c);
    }

    /**
     * @return number of players in game
     */
    synchronized public int getPlayersNumber() {
        return gameTable.getPlayers().size();
    }

    /**
     * Persistence main method: restarts game from disk. Basically the equivalent of {@link GameInitializer} for a reloaded game.
     * @param connections Contains every player's connection, in order from first to last.
     */
    synchronized public void restartFromDisk(ArrayList<SocketClientConnection> connections) {
        SocketClientConnection c1 = connections.get(0);
        SocketClientConnection c2 = connections.get(1);
        SocketClientConnection c3 = null;
        if (connections.size() == 3) c3 = connections.get(2);
        Player player1 = gameTable.getPlayers().get(0);
        Player player2 = gameTable.getPlayers().get(1);
        Player player3 = null;
        if (c3 != null) player3 = gameTable.getPlayers().get(2);
        c1.setPlayer(player1);
        c2.setPlayer(player2);
        if (c3 != null) c3.setPlayer(player3);
        View player1View = new RemoteView(player1, c1, gameTable);
        View player2View = new RemoteView(player2, c2, gameTable);
        View player3View = null;
        if (c3 != null) {
            player3View = new RemoteView(player3, c3, gameTable);
        }
        gameTable.addPropertyChangeListener(player1View);
        gameTable.addPropertyChangeListener(player2View);
        if (c3 != null) {
            gameTable.addPropertyChangeListener(player3View);
        }
        player1View.addPropertyChangeListener(this);
        player2View.addPropertyChangeListener(this);
        if (logger.isDebugEnabled()) {
            gameTable.addPropertyChangeListener(Server.getServerView());
        }
        if (c3 != null) {
            player3View.addPropertyChangeListener(this);
        }

        c1.setReady();
        c2.setReady();
        if (c3 != null) {
            c3.setReady();
        }
        int size = (c3 == null ? 2 : 3);
        c1.send(new InitMessage(gameTable.getPlayerIndex(player1), size, gameTable.getGameIndex(), GameTable.completeGodList.indexOf(c1.getPlayer().getGod()), GameTable.completeGodList.indexOf(c2.getPlayer().getGod()), c3 != null ? GameTable.completeGodList.indexOf(c3.getPlayer().getGod()) : -1));
        c2.send(new InitMessage(gameTable.getPlayerIndex(player2), size, gameTable.getGameIndex(), GameTable.completeGodList.indexOf(c1.getPlayer().getGod()), GameTable.completeGodList.indexOf(c2.getPlayer().getGod()), c3 != null ? GameTable.completeGodList.indexOf(c3.getPlayer().getGod()) : -1));
        if (c3 != null) {
            c3.send(new InitMessage(gameTable.getPlayerIndex(player3), size, gameTable.getGameIndex(), GameTable.completeGodList.indexOf(c1.getPlayer().getGod()), GameTable.completeGodList.indexOf(c2.getPlayer().getGod()), GameTable.completeGodList.indexOf(c3.getPlayer().getGod())));
        }
        GameStateMessage message = gameTable.getGameState();
        c1.send(message);
        c2.send(message);
        if (c3 != null) c3.send(message);
        c1.send(gameTable.getBoardCopy());
        c2.send(gameTable.getBoardCopy());
        if (c3 != null) c3.send(gameTable.getBoardCopy());
        gameTable.resetMoveTimer();
    }

}