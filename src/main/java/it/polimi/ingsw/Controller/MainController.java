package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Model.Cell;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MainController implements PropertyChangeListener {

    /* observer pattern */

    private String news;
    private Cell cellNews;

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) { // equivalente di update
        Object obj = propertyChangeEvent.getNewValue();
        if (obj instanceof String) this.setNews((String) obj);
        else this.setCellNews((Cell) obj);
        System.out.println(this.news); // DEBUG TODO remove
    }
    private void setNews(String s){
        this.news = s;
    }

    private void setCellNews(Cell s){
        this.cellNews = s;
    }

    public String getNews() {
        return this.news;
    }

    public Cell getCellNews(){
        return this.cellNews;
    }

    /* fine observer pattern */

    public MainController(){//TODO (?)
    }

}
