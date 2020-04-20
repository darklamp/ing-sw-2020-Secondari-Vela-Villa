package it.polimi.ingsw.View;


import it.polimi.ingsw.Controller.Exceptions.IllegalTurnStateException;
import it.polimi.ingsw.Model.News;
import it.polimi.ingsw.Model.Player;
import it.polimi.ingsw.Network.ClientConnection;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class RemoteView extends View {

    private class MessageReceiver implements PropertyChangeListener {

        private News news;

        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) { // equivalente di update
            Object obj = propertyChangeEvent.getNewValue();
            String name = propertyChangeEvent.getPropertyName();
            setNews((News) obj);
            System.out.println("Received: " + "news"); //?
            try {
//                handleMove(Integer.parseInt(inputs[0]), Integer.parseInt(inputs[1]));

                //todo move handling
            } catch (IllegalArgumentException e) {
                clientConnection.asyncSend("Error!");

            }
        }

        private void setNews(News news){
            this.news = news;
        }

        public News getNews(){
            return this.news;
        }

    }

    private ClientConnection clientConnection;

    public RemoteView(Player player, String opponent, ClientConnection c) {
        super(player);
        this.clientConnection = c;
        c.addPropertyChangeListener(new MessageReceiver());
        c.asyncSend("Your opponent is: " + opponent);

    }

    @Override
    protected void showMessage(Object message) {
        clientConnection.asyncSend(message);
    }

    @Override
    public void update(MoveMessage message)
    {
        showMessage(message.getBoard());
        String resultMsg = "";
        boolean gameOver = message.getBoard().isGameOver(message.getPlayer().getMarker());
        boolean draw = message.getBoard().isFull();
        if (gameOver) {
            if (message.getPlayer() == getPlayer()) {
                resultMsg = gameMessage.winMessage + "\n";
            } else {
                resultMsg = gameMessage.loseMessage + "\n";
            }
        }
        else {
            if (draw) {
                resultMsg = gameMessage.drawMessage + "\n";
            }
        }
        if(message.getPlayer() == getPlayer()){
            resultMsg += gameMessage.waitMessage;
        }
        else{
            resultMsg += gameMessage.moveMessage;
        }

        showMessage(resultMsg);
    }

}
