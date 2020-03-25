package it.polimi.ingsw.Controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MainController implements PropertyChangeListener {

    /* observer pattern */

    private String news;

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) { // equivalente di update
        this.setNews((String) propertyChangeEvent.getNewValue());
        System.out.println(this.news); // DEBUG TODO remove
    }
    private void setNews(String s){
        this.news = s;
    }

    public String getNews() {
        return this.news;
    }

    /* fine observer pattern */

    public MainController(){//TODO (?)
    }

}
