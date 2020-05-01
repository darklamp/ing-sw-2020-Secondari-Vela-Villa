package it.polimi.ingsw.View;


import it.polimi.ingsw.Controller.Exceptions.IllegalTurnStateException;
import it.polimi.ingsw.Model.News;
import it.polimi.ingsw.Model.Player;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class View implements PropertyChangeListener {

    final Player player;

    News modelNews; /* news coming from model */

    News controllerNews; /* news to be sent to controller */

    final PropertyChangeSupport support = new PropertyChangeSupport(this); /** Listener helper object **/

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

    private News getModelNews(){
        return this.modelNews;
    }

    public void setModelNews(News news){
        this.modelNews = news;
    }

    protected View(Player player){
        this.player = player;
    }

    protected Player getPlayer(){
        return player;
    }

    protected abstract void showMessage(Object message);

    public void reportError(String message){
        showMessage(message);
    }

}
