package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Model.Exceptions.DemeterException;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.News;

import static it.polimi.ingsw.Model.TurnState.*;

public class BuildController {

    public void handleBuild(News news) {
        try{
            if (news.getBuilder() != GameTable.getInstance().getCurrentBuilder()) throw new InvalidBuildException(); /* trying to build using the builder which I didn't previously move */
            news.getCell().setHeight(news.getBuilder(), news.getHeight());
            GameTable.getInstance().getCurrentPlayer().setState(PASS);
            GameTable.getInstance().setNews(news,"BUILDOK");
        }catch (DemeterException e) {
            GameTable.getInstance().getCurrentPlayer().setState(BUILDORPASS);
            GameTable.getInstance().setNews(news,"BUILDOK");
        } catch (InvalidBuildException e) {
            GameTable.getInstance().setNews(news, "BUILDKO");
        }
    }
}
