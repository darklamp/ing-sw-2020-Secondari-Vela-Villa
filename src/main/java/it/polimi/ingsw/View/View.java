package it.polimi.ingsw.View;


import it.polimi.ingsw.Controller.Exceptions.IllegalTurnStateException;
import it.polimi.ingsw.Model.News;
import it.polimi.ingsw.Model.Player;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class View implements PropertyChangeListener {

    private Player player;

    private News modelNews; /* news coming from model */

    News controllerNews; /* news to be sent to controller */

    PropertyChangeSupport support; /** Listener helper object **/

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    public void setControllerNews(News news, String type) {
        support.firePropertyChange(type, this.controllerNews, news);
        this.controllerNews = news;
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) { // equivalente di update
        Object obj = propertyChangeEvent.getNewValue();
        String name = propertyChangeEvent.getPropertyName();
        this.setModelNews((News) obj);

    }

    public News getModelNews(){
        return this.modelNews;
    }

    private void setModelNews(News news){
        this.modelNews = news;
    }

    protected View(Player player){
        this.player = player;
    }

    protected Player getPlayer(){
        return player;
    }

    protected abstract void showMessage(Object message);

    void handleMove(int row, int column) {
        System.out.println(row + " " + column);
        notify(new PlayerMove(player, row, column, this));
    }

    public void reportError(String message){
        showMessage(message);
    }

}
