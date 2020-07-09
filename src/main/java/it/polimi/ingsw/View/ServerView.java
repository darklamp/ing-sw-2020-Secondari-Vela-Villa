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
 * Server-side view which observes games.
 * Used as a filter to log every move/whatever we feel like logging.
 */
public class ServerView extends View {

    private final Logger logger = LoggerFactory.getLogger(ServerView.class);

    public ServerView() {
        super(null);
    }

    /**
     * @see PropertyChangeListener
     **/
    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        News news = (News) propertyChangeEvent.getNewValue();
        GameTable gameTable = (GameTable) propertyChangeEvent.getSource();
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

