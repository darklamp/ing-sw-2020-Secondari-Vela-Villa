package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Model.Exceptions.ArtemisException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;
import it.polimi.ingsw.Model.Exceptions.PanException;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.News;

import static it.polimi.ingsw.Model.TurnState.BUILD;

public class MoveController {

    public void handleMove(News news) {
        try{
            news.getBuilder().setPosition(news.getCell());
            GameTable.getInstance().getCurrentPlayer().setState(BUILD);
            GameTable.getInstance().setCurrentBuilder(news.getBuilder());
            GameTable.getInstance().setNews(news,"MOVEOK");
        }
        catch (InvalidMoveException e){
            GameTable.getInstance().setNews(news,"MOVEKO");
        }
        catch (ArtemisException e){

        }
        catch (PanException e){

        }

    }

}
