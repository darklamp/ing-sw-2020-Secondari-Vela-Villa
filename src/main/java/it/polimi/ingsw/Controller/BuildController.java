package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Model.Exceptions.*;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.Hephaestus;
import it.polimi.ingsw.Model.News;

import static it.polimi.ingsw.Client.ClientState.*;

/**
 * Controller responsible for handling builds
 */
public class BuildController {

    private final GameTable gameTable;

    public void handleBuild(News news) throws NoMoreMovesException {
        String s = "BUILDKO";
        try{
            if (news.getBuilder(gameTable) != gameTable.getCurrentBuilder()) throw new InvalidBuildException(); /* trying to build using the builder which I didn't previously move */
            news.getCell(gameTable).setHeight(news.getBuilder(gameTable),news.getHeight());
            gameTable.getCurrentPlayer().setState(WAIT);
            news.setRecipients(gameTable.getPlayerConnections());
            gameTable.setNews(news,"BUILDOK");
            gameTable.nextTurn();
            news = new News();
            news.setRecipients(gameTable.getCurrentPlayer());
            s = "YOURTURN";
        } catch (InvalidBuildException ignored) {
        } catch (DemeterException | HephaestusException e) {
            gameTable.getCurrentPlayer().setState(BUILDORPASS);
            s = "BUILDOK";
        } catch (PrometheusException e){
            gameTable.getCurrentPlayer().setState(MOVE);
            s = "BUILDOK";
        }
        finally {
            gameTable.setNews(news,s);
        }
    }

    public BuildController(GameTable gameTable){
        this.gameTable = gameTable;
    }
}
