package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Controller.Exceptions.IllegalTurnStateException;
import it.polimi.ingsw.Model.Exceptions.NoMoreMovesException;
import it.polimi.ingsw.Model.Exceptions.WinnerException;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.News;
import it.polimi.ingsw.Model.Player;
import it.polimi.ingsw.Network.GameInitializer;
import it.polimi.ingsw.Network.SocketClientConnection;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Manage the news received.
 */

public class MainController implements PropertyChangeListener {

    private Player currentPlayer;
    private final BuildController buildController;
    private final MoveController moveController;
    private final GameTable gameTable;
    private GameInitializer gameInitializer;
    private News news;
    private ControllerState state = ControllerState.PLAY;

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
                else switch (state) {
                    case PLAY -> {
                        isLegalState(name,currentPlayer.getState());
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
                    }
                }
            }
            catch(IllegalTurnStateException e){ // player isn't in a legal state for the move
                news.setRecipients(gameTable.getCurrentPlayer());
                gameTable.setNews(news, "ILLEGALSTATE");
            }
            catch (WinnerException e){
                SocketClientConnection winner = gameTable.getPlayerConnections().get(0);
                for (SocketClientConnection c : gameTable.getPlayerConnections()){
                    if (c.getPlayer() == e.getPlayer()) {
                        winner = c;
                        break;
                    }
                }
                News n = new News(null, winner);
                n.setRecipients(gameTable.getPlayerConnections());
                gameTable.setNews(n,"WIN");
                gameTable.closeGame();
            }
            catch (NoMoreMovesException e){
                if (gameTable.getPlayerConnections().size() == 2) {
                    SocketClientConnection winner = gameTable.getPlayerConnections().get(0);
                    for (SocketClientConnection c : gameTable.getPlayerConnections()){
                        if (c.getPlayer() != e.getPlayer()) {
                            winner = c;
                            break;
                        }
                    }
                    News n = new News(null, winner);
                    n.setRecipients(gameTable.getPlayerConnections());
                    gameTable.setNews(n,"WIN");
                    gameTable.closeGame();
                }
                else {
                    gameTable.removePlayer(e.getPlayer());
                }
            }
        }

    }


    private void setNews(News news){
        this.news = news;
    }

    public News getNews(){
        return this.news;
    }

    /**
     * @param name Turn state from Server side
     * @param turn Turn state from Client side
     * Checks they are matching, otherwise
     * @throws IllegalTurnStateException
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
        this.gameTable = gameTable;
        this.buildController = new BuildController(gameTable);
        this.moveController = new MoveController(gameTable);
    }

    public void setGameInitializer(GameInitializer gameInitializer) {
        if (this.gameInitializer == null) this.gameInitializer = gameInitializer;
    }


}

enum ControllerState{
    PLAY;
}