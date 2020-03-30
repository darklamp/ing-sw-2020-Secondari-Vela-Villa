package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Model.Exceptions.DemeterException;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.News;

public class BuildController {

    public final GameTable gameTable;

    public BuildController(GameTable g) {
        this.gameTable = g;
    }

    public void handleBuild(News news) throws InvalidBuildException, DemeterException {
        news.getCell().setHeight(news.getBuilder(), news.getHeight());
        gameTable.setNews(news,"BUILDOK");
    }
}
