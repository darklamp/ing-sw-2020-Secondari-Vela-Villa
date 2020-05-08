package it.polimi.ingsw.View;


import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.News;
import it.polimi.ingsw.Model.Player;
import it.polimi.ingsw.Network.SocketClientConnection;
import it.polimi.ingsw.Network.ServerMessage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RemoteView extends View {

    private class MessageReceiver implements PropertyChangeListener {

        private News news;

        private final List<String> validTypes = Arrays.asList("MOVE","BUILD","PASS");

        /** receive message from SocketClientConnection -> parse -> forward to MainController **/
        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) { // equivalente di update
            Object obj = propertyChangeEvent.getNewValue();
            setNews((News) obj);
            System.out.println("Received message from: " + news.getSender().getPlayer().getNickname() +"\n");
            String name = propertyChangeEvent.getPropertyName();
            if (name.equals("PLAYERTIMEOUT")){
                setControllerNews(news,"PLAYERTIMEOUT");
            }
            else if (name.equals("ABORT")){
                setControllerNews(news,"ABORT");
            }
            else isValidNews(news);
        }


        /**
         * Parses client input
         * Input is formatted as such:
         * if TYPE == PASS --> PASS //TODO define if pass should even be a command
         * else  (MOVE||BUILD)$$COORD1$$COORD2$$BUILDERNUMBER
         *
         * if input is invalid, news is set to invalid, so controller returns an error message tot the client
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
                }
                else{
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
                                }
                                catch (NumberFormatException e){
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
                                                }
                                                else news.setCoords(i,j,k-1);
                                                setControllerNews(news, "BUILD");
                                                break;
                                            }
                                        }
                                    }
                                }
                                catch (NumberFormatException e){
                                    news.setInvalid();
                                }
                            }
                            news.setInvalid();
                        }
                    }
                }
            }
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

    @Override
    protected void showMessage(Object message) {
        socketClientConnection.asyncSend(message);
    }

    /** this method gets called when the model sends a news; its purpose is to update the view.
     **/
    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        News news = (News) propertyChangeEvent.getNewValue();
        String name = propertyChangeEvent.getPropertyName();
        ArrayList<SocketClientConnection> c = news.getRecipients();
        if (c == null || c.contains(this.socketClientConnection)) {
            switch (name){
                case "NOTYOURTURN" -> this.socketClientConnection.asyncSend(ServerMessage.notYourTurn);
                case "ILLEGALSTATE","INVALIDNEWS","MOVEKO" -> this.socketClientConnection.asyncSend(ServerMessage.invalidMove);
                case "MOVEOK","BUILDOK", "PASSOK" -> {
                    this.socketClientConnection.send(gameTable.getBoardCopy());
                    this.socketClientConnection.send(this.player.getState());
                }
                case "YOURTURN" -> this.socketClientConnection.send(this.player.getState());
                case "WIN" -> {
                    if (news.getSender() == this.socketClientConnection) {
                        this.socketClientConnection.asyncSend(gameTable.getBoardCopy());
                        this.socketClientConnection.send(ClientState.WIN);
                    }
                    else this.socketClientConnection.send(ClientState.LOSE);
                }
                case "ABORT" -> this.socketClientConnection.send(ServerMessage.abortMessage);
                default -> this.socketClientConnection.asyncSend(ServerMessage.genericErrorMessage);
            }
        }
    }

}

