package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Model.Cell;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.Player;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MainController implements PropertyChangeListener {

    private Player currentPlayer;

    /* observer pattern */

    private String news;
    private Cell cellNews;

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) { // equivalente di update
        Object obj = propertyChangeEvent.getNewValue();
        if (obj instanceof String) {
            this.setNews((String) obj);
            if (getNews() == "NextTurn") {
                try{
                    GameTable.checkWinner();
                }
                catch(GameOverException e){
                    //TODO: handle game won
                }
                GameTable.nextTurn(); // se il giocatore ha passato il turno e non ha vinto
                currentPlayer = GameTable.getCurrentPlayer();
            }
        }
        else {
            this.setCellNews((Cell) obj);
            //TODO
        }
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
        this.currentPlayer = null; // TODO
    }

}
