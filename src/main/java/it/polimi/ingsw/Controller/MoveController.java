package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Model.Exceptions.ArtemisException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;
import it.polimi.ingsw.Model.Exceptions.PanException;
import it.polimi.ingsw.Model.Exceptions.PrometeusException;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.News;

import static it.polimi.ingsw.Model.TurnState.BUILD;
import static it.polimi.ingsw.Model.TurnState.MOVEORBUILD;

public class MoveController {
    private final GameTable gameTable;

    public MoveController(GameTable gameTable){
        this.gameTable = gameTable;
    }

    public void handleMove(News news) {
        try{
            news.getBuilder(gameTable).setPosition(news.getCell(gameTable));
            gameTable.getCurrentPlayer().setState(BUILD);
            gameTable.setCurrentBuilder(news.getBuilder(gameTable));
            gameTable.setNews(news,"MOVEOK");
        }
        catch (ArtemisException e){
            gameTable.getCurrentPlayer().setState(MOVEORBUILD);
            gameTable.setCurrentBuilder(news.getBuilder(gameTable));
            gameTable.setNews(news,"MOVEOK");
        } catch (PanException e){

        } catch (PrometeusException e) {
        }
        catch (Exception e){
            gameTable.setNews(news,"MOVEKO");
        }

    }

}
