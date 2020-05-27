package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Controller.Exceptions.IllegalTurnStateException;
import it.polimi.ingsw.Model.Exceptions.NoMoreMovesException;
import it.polimi.ingsw.Model.Exceptions.WinnerException;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.News;
import it.polimi.ingsw.Model.Player;
import it.polimi.ingsw.Network.GameInitializer;
import it.polimi.ingsw.Network.GameStateMessage;
import it.polimi.ingsw.Network.SocketClientConnection;
import it.polimi.ingsw.ServerMain;
import it.polimi.ingsw.View.RemoteView;
import it.polimi.ingsw.View.View;

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
    private FileOutputStream fileOutputStream;
    private News news;

    @Override
    synchronized public void propertyChange(PropertyChangeEvent propertyChangeEvent) { // equivalente di update
        Object obj = propertyChangeEvent.getNewValue();
        String name = propertyChangeEvent.getPropertyName();
        this.setNews((News) obj);
        if (!news.isValid()) {
            news.setRecipients(news.getSender().getPlayer());
            gameTable.setNews(news, "INVALIDNEWS");
            return;
        }
        currentPlayer = gameTable.getCurrentPlayer();
        if (currentPlayer != news.getSender().getPlayer()) gameTable.setNews(news, "NOTYOURTURN");
        else if (name.equals("ABORT")) {
            News n = new News(null, gameTable.getPlayerConnections().get(0));
            gameTable.setNews(n, "ABORT");
            gameTable.closeGame();
        } else {
            try {
                if (name.equals("PLAYERTIMEOUT")) throw new NoMoreMovesException(news.getSender().getPlayer());
                else {
                    isLegalState(name, currentPlayer.getState());
                    switch (name) {
                        case "PASS" -> {
                            gameTable.nextTurn();
                            currentPlayer = gameTable.getCurrentPlayer();
                            gameTable.setNews(news, "PASSOK");
                        }
                        case "BUILD" -> buildController.handleBuild(news);
                        case "MOVE" -> moveController.handleMove(news);
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
                        break;
                    }
                }
                winner.getPlayer().setState(ClientState.WIN);
                News n = new News(null, winner);
                n.setRecipients((ArrayList<SocketClientConnection>) null);
                gameTable.setNews(n, "WIN");
                gameTable.closeGame();
            } catch (NoMoreMovesException e) {
                if (gameTable.getPlayerConnections().size() == 2) {
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
                } else {
                    try {
                        gameTable.removePlayer(e.getPlayer(), false);
                    } catch (NoMoreMovesException ignored) {
                    }
                }
            }
        }

    }


    private void setNews(News news) {
        this.news = news;
    }

    public News getNews() {
        return this.news;
    }

    /**
     * @param name Turn state from Client side
     * @param turn Turn state from Server side
     * @throws IllegalTurnStateException if not matching
     */
    private static void isLegalState(String name, ClientState turn) throws IllegalTurnStateException {
        switch (turn) {
            case BUILD:
                if (!name.equals("BUILD")) throw new IllegalTurnStateException();
                break;
            case MOVE:
                if (!name.equals("MOVE")) throw new IllegalTurnStateException();
                break;
            case MOVEORBUILD:
                if (name.equals("PASS")) throw new IllegalTurnStateException();
                break;
            case BUILDORPASS:
                if (name.equals("MOVE")) throw new IllegalTurnStateException();
                break;
            case WAIT:
                if (!name.equals("PASS")) throw new IllegalTurnStateException();
                break;
            default:
                throw new IllegalTurnStateException();
        }
    }

    /**
     * Constructor for the MainController.Takes gameTable as argument.
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

    public void consoleKickPlayer(String playerNick) {
        gameTable.getPlayers().stream().filter(c -> c.getNickname().equals(playerNick)).findFirst().ifPresent(player -> {
            try {
                gameTable.removePlayer(player, true);
            } catch (NoMoreMovesException ignored) {
            }
        });
    }

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
            if (ServerMain.verbose()) e.printStackTrace();
        }
    }

    synchronized public int containsPlayer(String playerNick) {
        for (Player p : gameTable.getPlayers()) {
            if (p.getNickname().equals(playerNick)) return gameTable.getPlayerIndex(p);
        }
        return -1;
    }

    synchronized public void setPlayerFromDisk(String name, SocketClientConnection c) {
        Player p = null;
        for (Player p1 : gameTable.getPlayers()) if (p1.getNickname().equals(name)) p = p1;
        assert p != null;
        p.setConnection(c);
    }

    synchronized public int getPlayersNumber() {
        return gameTable.getPlayers().size();
    }

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
        if (c3 != null) {
            player3View.addPropertyChangeListener(this);
        }
        c1.setReady();
        c2.setReady();
        if (c3 != null) {
            c3.setReady();
        }
        int size = (c3 == null ? 2 : 3);
        c1.send("[INIT]@@@" + gameTable.getPlayerIndex(player1) + "@@@" + size + "@@@" + gameTable.getGameIndex() + "@@@" + 0);
        c2.send("[INIT]@@@" + gameTable.getPlayerIndex(player2) + "@@@" + size + "@@@" + gameTable.getGameIndex() + "@@@" + 0);
        if (c3 != null) {
            c3.send("[INIT]@@@" + gameTable.getPlayerIndex(player3) + "@@@" + size + "@@@" + gameTable.getGameIndex() + "@@@" + 0);
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