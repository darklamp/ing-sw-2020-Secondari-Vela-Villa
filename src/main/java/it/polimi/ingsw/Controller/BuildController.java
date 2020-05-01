package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Model.Exceptions.DemeterException;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.News;

import static it.polimi.ingsw.Model.TurnState.*;

public class BuildController {

    private GameTable gameTable;

    public void handleBuild(News news) {
        try{
            if (news.getBuilder(gameTable) != gameTable.getCurrentBuilder()) throw new InvalidBuildException(); /* trying to build using the builder which I didn't previously move */
            news.getCell(gameTable).setHeight(news.getBuilder(gameTable), news.getHeight());
            gameTable.getCurrentPlayer().setState(NOOP);
            gameTable.setNews(news,"BUILDOK");
        }catch (DemeterException e) {
            gameTable.getCurrentPlayer().setState(BUILDORPASS);
            gameTable.setNews(news,"BUILDOK");
        } catch (InvalidBuildException e) {
            gameTable.setNews(news, "BUILDKO");
        }
    }

    public BuildController(GameTable gameTable){
        this.gameTable = gameTable;
    }
}
