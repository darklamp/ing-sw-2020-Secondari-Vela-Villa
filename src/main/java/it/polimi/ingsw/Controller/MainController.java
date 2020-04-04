package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Controller.Exceptions.IllegalTurnStateException;
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
    private News news;

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) { // equivalente di update
        Object obj = propertyChangeEvent.getNewValue();
        String name = propertyChangeEvent.getPropertyName();
        this.setNews((News) obj);
        currentPlayer = gameTable.getCurrentPlayer();
        // TODO: controllo bene che mossa provenga da persona giusta (come?)
        if (name == null) gameTable.setNews(new News(), "INVALIDNEWS");
        else {
            try {
                isLegalState(name,currentPlayer.getState());
                switch (name) {
                    case "PASS" -> {
                        gameTable.nextTurn();
                        currentPlayer = gameTable.getCurrentPlayer(); // se il giocatore ha passato il turno e non ha vinto
                        gameTable.setNews(new News(), "PASSOK");
                    }
                    case "BUILD" -> buildController.handleBuild(news);
                    case "MOVE" -> moveController.handleMove(news);
                    default -> throw new IllegalTurnStateException();
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
            case MOVEORBUILD:
                if (name.equals("PASS")) throw new IllegalTurnStateException();
                gameTable.setCurrentBuilder(news.getBuilder());
                break;
            case BUILDORPASS:
                if (name.equals("MOVE")) throw new IllegalTurnStateException();
                break;
            case PASS:
                if (!name.equals("PASS")) throw new IllegalTurnStateException();
                break;
            default:
                throw new IllegalTurnStateException();
        }
    }

    public MainController(int playersNumber){//TODO nota: bisogna controllare quando si usa il metodo che playersNumber sia valido
        this.currentPlayer = null;
        this.news = null;
        this.gameTable = GameTable.getInstance(playersNumber);
        this.buildController = new BuildController();
        this.moveController = new MoveController();
    }

}
