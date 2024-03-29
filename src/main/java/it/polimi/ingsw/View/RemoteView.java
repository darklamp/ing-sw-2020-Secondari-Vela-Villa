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
import it.polimi.ingsw.Model.Player;
import it.polimi.ingsw.Network.Messages.ServerMessage;
import it.polimi.ingsw.Network.SocketClientConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Server-side client view.
 */
public class RemoteView extends View {

    /**
     * Very basic count for error messages. Once a client reaches the threshold it gets kicked
     **/
    private int errorMessageCount = 0;
    private static final int MAX_ERROR_MESSAGE_COUNT = 10000;

    /**
     * Class responsible for parsing messages.
     */
    private class MessageReceiver implements PropertyChangeListener {

        private News news;

        private final List<String> validTypes = Arrays.asList("MOVE", "BUILD", "PASS");
        private final Logger logger = LoggerFactory.getLogger(MessageReceiver.class);

        /**
         * receive message from SocketClientConnection -> parse -> forward to MainController
         **/
        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) { // equivalente di update
            Object obj = propertyChangeEvent.getNewValue();
            setNews((News) obj);
            //     logger.debug("Received message from: {}", news.getSender().getPlayer().getNickname());
            String name = propertyChangeEvent.getPropertyName();
            if (name.equals("PLAYERTIMEOUT")) {
                setControllerNews(news, "PLAYERTIMEOUT");
            } else if (name.equals("ABORT")) {
                setControllerNews(news, "ABORT");
            } else isValidNews(news);
        }


        /**
         * Parses client input
         * Input is formatted as such:
         * if TYPE == PASS --> PASS
         * else  (MOVE||BUILD)$$COORD1$$COORD2$$BUILDERNUMBER
         * <p>
         * if input is invalid, news is set to invalid, so controller returns an error message to the client
         *
         * @param news to be parsed
         */
        private void isValidNews(News news){
            String s = news.getString();
            if (s == null) news.setInvalid();
            else {
                String[] args = s.split("@@@");
                if (args[0] == null || !validTypes.contains(args[0])) {
                    news.setInvalid();
                } else{
                    switch (args[0]){
                        case "PASS" -> setControllerNews(news, "PASS");
                        case "MOVE" -> {
                            if (args[1] != null && args[2] != null && args[3] != null){
                                int i; //xCoord
                                int j; //yCoord
                                int k; //builder number (1/2)
                                try{
                                    i = Integer.parseInt(args[1]);
                                    if (i < 5 && i >= 0){
                                        j = Integer.parseInt(args[2]);
                                        if (j < 5 && j >= 0){
                                            k = Integer.parseInt(args[3]);
                                            if (k == 2 || k == 1){
                                                news.setCoords(i,j,k-1);
                                                setControllerNews(news, "MOVE");
                                                break;
                                            }
                                        }
                                    }
                                } catch (NumberFormatException e){
                                    news.setInvalid();
                                }
                            }
                            news.setInvalid();
                        }
                        case "BUILD" -> {
                            if (args[1] != null && args[2] != null && args[3] != null){
                                int i; //xCoord
                                int j; //yCoord
                                int k; //builder number (1/2)
                                try{
                                    i = Integer.parseInt(args[1]);
                                    if (i < 5 && i >= 0){
                                        j = Integer.parseInt(args[2]);
                                        if (j < 5 && j >= 0){
                                            k = Integer.parseInt(args[3]);
                                            if (k == 2 || k == 1){
                                                if (args.length == 5 && args[4] != null){
                                                    int h = Integer.parseInt(args[4]);
                                                    if (h < 0 || h > 3) throw new NumberFormatException();
                                                    else news.setCoords(i,j,k-1,h);
                                                } else news.setCoords(i,j,k-1);
                                                setControllerNews(news, "BUILD");
                                                break;
                                            }
                                        }
                                    }
                                } catch (NumberFormatException e) {
                                    news.setInvalid();
                                }
                            }
                            news.setInvalid();
                        }
                    }
                }
            }
            if (!news.isValid()) setControllerNews(news, "INVALID");
        }

        private void setNews(News news){
            this.news = news;
        }
    }

    private final SocketClientConnection socketClientConnection;

    private final GameTable gameTable;

    public RemoteView(Player player, SocketClientConnection c, GameTable gameTable) {
        super(player);
        this.gameTable = gameTable;
        this.socketClientConnection = c;
        c.addPropertyChangeListener(new MessageReceiver());
    }

    /**
     * @see PropertyChangeListener
     **/
    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        News news = (News) propertyChangeEvent.getNewValue();
        String name = propertyChangeEvent.getPropertyName();
        ArrayList<SocketClientConnection> c = news.getRecipients();
        if (c == null || c.contains(this.socketClientConnection)) {
            switch (name) {
                case "NOTYOURTURN" -> {
                    this.socketClientConnection.send(ServerMessage.notYourTurn);
                    this.socketClientConnection.send(gameTable.getGameState());
                }
                case "ILLEGALSTATE", "INVALIDNEWS", "MOVEKO", "BUILDKO" -> this.socketClientConnection.send(ServerMessage.invalidNews);
                case "MOVEOK", "BUILDOK", "PASSOK", "TURN", "WIN" -> {
                    this.socketClientConnection.send(gameTable.getGameState());
                    this.socketClientConnection.send(gameTable.getBoardCopy());
                }
                case "ABORT" -> this.socketClientConnection.send(ServerMessage.abortMessage);
                case "PLAYERKICKED" -> this.socketClientConnection.send(news.getString());
                default -> {
                    this.socketClientConnection.send(ServerMessage.genericErrorMessage);
                    // if (errorMessageCount > MAX_ERROR_MESSAGE_COUNT) this.socketClientConnection.closeConnection();
                    // else errorMessageCount += 1;
                }
            }
        }
    }

}

