package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Model.Exceptions.*;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.News;

import static it.polimi.ingsw.Client.ClientState.BUILD;
import static it.polimi.ingsw.Client.ClientState.MOVEORBUILD;

/**
 * Controller responsible for handling move
 */
public class MoveController {
    private final GameTable gameTable;

    public MoveController(GameTable gameTable){
        this.gameTable = gameTable;
    }

    void handleMove(News news) throws WinnerException {
        try{
            news.getBuilder(gameTable).setPosition(news.getCell(gameTable));
            gameTable.getCurrentPlayer().setState(BUILD);
            gameTable.setCurrentBuilder(news.getBuilder(gameTable));
            gameTable.setNews(news,"MOVEOK");
        }
        catch (PanException e){

        }
        catch (ArtemisException e){
            gameTable.getCurrentPlayer().setState(MOVEORBUILD);
            gameTable.setCurrentBuilder(news.getBuilder(gameTable));
            gameTable.setNews(news,"MOVEOK");
        }
        catch (PrometeusException e) {
        }
        catch (InvalidMoveException e){
            gameTable.setNews(news,"MOVEKO");
        }

    }



}
