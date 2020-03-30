package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Model.Exceptions.*;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.News;
import it.polimi.ingsw.Model.Player;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MainController implements PropertyChangeListener {

    private Player currentPlayer;
    private BuildController buildController;
    private GameTable gameTable;

    /* observer pattern */

   // private String news;
  //  private Cell cellNews;
    private News news;

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) { // equivalente di update
        Object obj = propertyChangeEvent.getNewValue();
        String name = propertyChangeEvent.getPropertyName();
        this.setNews((News) obj);
        currentPlayer = gameTable.getCurrentPlayer();
        //TODO: controllare che si faccia prima move e poi build, o nel caso di efesto (build) move build
        // creare casi di inizializzione (dei, builder,...)
        switch (name) {
            case "PASS":
               /* try{
                    gameTable.checkWinner();
                }
                catch(GameOverException e){
                    //TODO: handle game won
                }*/
                gameTable.nextTurn();
                currentPlayer = gameTable.getCurrentPlayer(); // se il giocatore ha passato il turno e non ha vinto
                //TODO
                break;
            case "BUILD":
                    try {
                        buildController.handleBuild(news);
                    } catch (DemeterException e) {
                        //TODO nota: bisogna verificare di ricevere solo una eccezione di questo tipo
                    } catch (InvalidBuildException e) {
                        gameTable.setNews(news,"BUILDKO");
                        break;
                    }
                break;
            case "MOVE":
                    try {
                        moveController.handleMove(news);
                        news.getBuilder().setPosition(news.getCell());
                    } catch (InvalidMoveException e) {
                        // TODO notificare view
                    } catch (ArtemisException e) {
                        //TODO
                    }
                break;
            default:
                //TODO
        }
    }

    private void setNews(News news){
        this.news = news;
    }

    public News getNews(){
        return this.news;
    }

    /* fine observer pattern */

    public MainController(int playersNumber){//TODO (?)
        this.currentPlayer = null; // TODO
        this.news = null;
        this.gameTable = GameTable.getInstance(playersNumber);
        this.buildController = new BuildController(this.gameTable);
    }

}
