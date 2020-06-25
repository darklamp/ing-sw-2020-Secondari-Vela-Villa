package it.polimi.ingsw.View;


import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.News;
import it.polimi.ingsw.Network.SocketClientConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;


/**
 * Server-side client view, used like a filter to log every move/whatever we feel like logging.
 */
public class ServerView extends View {

    private final GameTable gameTable;
    private final Logger logger = LoggerFactory.getLogger(ServerView.class);

    public ServerView(GameTable g) {
        super(null);
        this.gameTable = g;
    }

    /**
     * @see PropertyChangeListener
     **/
    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        News news = (News) propertyChangeEvent.getNewValue();
        String name = propertyChangeEvent.getPropertyName();
        ArrayList<SocketClientConnection> c = news.getRecipients();
        switch (name) {
            case "NOTYOURTURN" -> logger.debug("[GI: {}] Player {} tried to move but it wasn't his turn.", gameTable.getGameIndex(), c.get(0).getPlayer().getNickname());
            case "ILLEGALSTATE", "INVALIDNEWS", "MOVEKO", "BUILDKO" -> logger.debug("[GI: {}] Player {} made an invalid move.", gameTable.getGameIndex(), c.get(0).getPlayer().getNickname());
            case "MOVEOK", "BUILDOK", "PASSOK", "WIN" -> logger.debug("[GI: {}] Player {} made a valid move. Next state: {}", gameTable.getGameIndex(), gameTable.getCurrentPlayer().getNickname(), gameTable.getCurrentPlayer().getState());
            case "TURN" -> logger.debug("[GI: {}] Turn changed. Now plays: {}", gameTable.getGameIndex(), gameTable.getCurrentPlayer().getNickname());
            case "ABORT" -> logger.debug("[GI: {}] Game is being closed.", gameTable.getGameIndex());
            case "PLAYERKICKED" -> logger.debug("[GI: {}] A player has been kicked.", gameTable.getGameIndex());
            default -> logger.debug("[GI: {}] Generic error from player {}.", gameTable.getGameIndex(), (c == null || c.get(0) == null || c.get(0).getPlayer() == null) ? "undefined" : c.get(0).getPlayer().getNickname());
        }
    }

}

