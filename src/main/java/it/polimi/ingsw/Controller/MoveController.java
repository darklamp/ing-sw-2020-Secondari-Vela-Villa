package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Model.Exceptions.ArtemisException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;
import it.polimi.ingsw.Model.Exceptions.PanException;
import it.polimi.ingsw.Model.Exceptions.PrometeusException;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.News;

import static it.polimi.ingsw.Model.TurnState.BUILD;

public class MoveController {
    private GameTable gameTable;

    public MoveController(GameTable gameTable){
        this.gameTable = gameTable;
    }

    public void handleMove(News news) {
        try{
            news.getBuilder(gameTable).setPosition(news.getCell());
            gameTable.getCurrentPlayer().setState(BUILD);
            gameTable.setCurrentBuilder(news.getBuilder(gameTable));
            gameTable.setNews(news,"MOVEOK");
        }
        catch (InvalidMoveException e){
            gameTable.setNews(news,"MOVEKO");
        }
        catch (ArtemisException e){

        } catch (PanException e){

        } catch (PrometeusException e) {
        }

    }

}
