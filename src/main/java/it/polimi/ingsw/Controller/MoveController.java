package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Model.Exceptions.ArtemisException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.News;

public class MoveController {
    public final GameTable gameTable;

    public MoveController(GameTable g) {
        this.gameTable = g;
    }

    public void handleMove(News news) throws  InvalidMoveException, ArtemisException {
        news.getBuilder().setPosition(news.getCell());
        gameTable.setNews(news,"MOVEOK");
    }
}
