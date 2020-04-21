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

        /** this method listens for messages from the client, to which it must react forwarding to the controller **/
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

    /** this method gets called when the model sends a news; its purpose is to update the view.
     **/
    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {


    }

}
