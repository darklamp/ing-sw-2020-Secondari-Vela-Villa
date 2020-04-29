package it.polimi.ingsw.View;


import it.polimi.ingsw.Model.News;
import it.polimi.ingsw.Model.Player;
import it.polimi.ingsw.Network.SocketClientConnection;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class RemoteView extends View {


    private class MessageReceiver implements PropertyChangeListener {

        private News news;

        /** this method listens for messages from the client, to which it must react forwarding to the controller **/
        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) { // equivalente di update
            Object obj = propertyChangeEvent.getNewValue();
            setNews((News) obj);
            System.out.println("Received: " + "news"); //?
            setControllerNews(news, "INPUT");
        }

        private void setNews(News news){
            this.news = news;
        }
    }

    private SocketClientConnection SocketClientConnection;

    public RemoteView(Player player, SocketClientConnection c) {
        super(player);
        this.SocketClientConnection = c;
        c.addPropertyChangeListener(new MessageReceiver());
       // c.asyncSend("Your opponent is: " + opponent);

    }

    @Override
    protected void showMessage(Object message) {
        SocketClientConnection.asyncSend(message);
    }

    /** this method gets called when the model sends a news; its purpose is to update the view.
     **/
    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {


    }

}

