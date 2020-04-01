package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Controller.Exceptions.IllegalTurnStateException;
import it.polimi.ingsw.Model.Exceptions.*;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.News;
import it.polimi.ingsw.Model.Player;
import it.polimi.ingsw.Model.TurnState;

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
        // TODO: fare bene gestione turni
        if (name == null) gameTable.setNews(new News(), "INVALIDNEWS");
        else {
            try {
                isLegalState(name,currentPlayer.getState());
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
                        //TODO NB: una volta inizializzata la partita queste news non possono più essere usate!
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
                        throw new IllegalTurnStateException();
                }
            }
            catch(IllegalTurnStateException e){ // player isn't in a legal state for the move
                gameTable.setNews(new News(), "ILLEGALSTATE");
            }
        }

    }

    private void setNews(News news){
        this.news = news;
    }

    public News getNews(){
        return this.news;
    }

    private void isLegalState(String name, TurnState turn) throws IllegalTurnStateException {
        switch(turn){
            case BUILD:
                if (!name.equals("BUILD")) throw new IllegalTurnStateException();
                break;
            case MOVE:
                if (!name.equals("MOVE")) throw new IllegalTurnStateException();
                break;
            case BOTH:
                break;
            case PASS:
                if (!name.equals("PASS")) throw new IllegalTurnStateException();
                break;
            default:
                throw new IllegalTurnStateException();
        }
    }

    /* fine observer pattern */

    public MainController(int playersNumber){//TODO nota: bisogna controllare quando si usa il metodo che playersNumber sia valido
        this.currentPlayer = null;
        this.news = null;
        this.gameTable = GameTable.getInstance(playersNumber);
        this.buildController = new BuildController(this.gameTable);
        this.moveController = new MoveController(this.gameTable);
    }

}
