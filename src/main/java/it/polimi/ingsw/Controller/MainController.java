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
    private MoveController moveController;
    private GameTable gameTable;

    /* observer pattern */

    private News news;

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) { // equivalente di update
        Object obj = propertyChangeEvent.getNewValue();
        String name = propertyChangeEvent.getPropertyName();
        this.setNews((News) obj);
        currentPlayer = gameTable.getCurrentPlayer();
        //TODO: check turno giusto !!   --> servirà fare un controllo tra currentPlayer e player che ha inviato news (info data dalla view)
        // TODO: controllare che si faccia prima move e poi build, o nel caso di efesto (build) move build
        // TODO: fare bene gestione turni
        if (name == null); //TODO ERRORE: news invalida
        else if (gameTable == null){
            /** in this case gametable is yet to be initialized **/
            try{
                gameTable = GameTable.getInstance(news.getNumber());
            }
            catch (InvalidPlayersNumberException e){

             }
        }
        else {
            try {
                isLegalState(name,player.getState());
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
                        break;
                    case "BUILD":
                        try {
                            buildController.handleBuild(news);
                        } catch (DemeterException e) {
                            //TODO nota: bisogna verificare di ricevere solo una eccezione di questo tipo
                        } catch (InvalidBuildException e) {
                            gameTable.setNews(news, "BUILDKO");
                        }
                        break;
                    case "MOVE":
                        try {
                            moveController.handleMove(news);
                            break;
                        } catch (InvalidMoveException e) {
                            gameTable.setNews(news, "MOVEKO");
                        } catch (ArtemisException e) {
                            //TODO
                        }
                        break;
                    case "SETPLAYERNAME":
                        //TODO
                        break;
                    case "SETGODLIST":
                        //TODO
                        break;
                    case "SETPLAYERGOD":
                        //TODO
                        break;
                    case "INITBUILDER":
                        //TODO
                        break;
                    default:
                        //TODO
                }
            }
            catch(NotAllowedExpection e){ // player isn't in a legal state for the move
                //TODO
            }
        }

    }

    private void setNews(News news){
        this.news = news;
    }

    public News getNews(){
        return this.news;
    }

    /* fine observer pattern */

    public MainController(){//TODO (?)
        this.currentPlayer = null; // TODO
        this.news = null;
        this.gameTable = null;
       // this.buildController = new BuildController(this.gameTable);
      //  this.moveController = new MoveController(this.gameTable);
    }

}
