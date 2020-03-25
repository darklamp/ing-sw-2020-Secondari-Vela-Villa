package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Model.Cell;
import it.polimi.ingsw.Model.Exceptions.*;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.Player;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MainController implements PropertyChangeListener {

    private Player currentPlayer;

    /* observer pattern */

   // private String news;
  //  private Cell cellNews;
    private Move news;

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) { // equivalente di update
        Object obj = propertyChangeEvent.getNewValue();
        String name = propertyChangeEvent.getPropertyName();
        this.setNews((Move) obj);
        currentPlayer = GameTable.getCurrentPlayer();
        //TODO: controllare che si faccia prima move e poi build, o nel caso di efesto (build) move build
        switch (name) {
            case "PASS":
                try{
                    GameTable.checkWinner();
                }
                catch(GameOverException e){
                    //TODO: handle game won
                }
                currentPlayer = GameTable.nextTurn(); // se il giocatore ha passato il turno e non ha vinto
                //TODO
                break;
            case "BUILD":
                while (true) {
                    try {
                        news.getCell().setHeight(news.getBuilder(), news.getHeight());
                        break;
                    } catch (DemeterException e) {
                        //TODO nota: bisogna verificare di ricevere solo una eccezione di questo tipo
                    } catch (InvalidBuildException e) {
                        // TODO notificare view
                        break;
                    }
                }
                //TODO
                break;
            case "MOVE":
                while (true) {
                    try {
                        news.getBuilder().setPosition(news.getCell());
                        break;
                    } catch (InvalidMoveException e) {
                        // TODO notificare view
                    } catch (ArtemisException e) {
                        //TODO
                    }
                }
                //TODO
                break;
            default:
                //TODO
        }
    }

    /*private void setNews(String s){
        this.news = s;
    }*/

    private void setNews(Move news){
        this.news = news;
    }

  /*  public String getNews() {
        return this.news;
    }*/

    public Move getNews(){
        return this.news;
    }

    /* fine observer pattern */

    public MainController(){//TODO (?)
        this.currentPlayer = null; // TODO
        this.news = null;
    }

}
