package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Model.Exceptions.*;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.News;

import static it.polimi.ingsw.Client.ClientState.*;

/**
 * Controller responsible for handling builds
 */
public class BuildController {

    private final GameTable gameTable;

    /**
     * @param news contains the cell where the player wants to build
     * @throws NoMoreMovesException see this exception in Model
     */
    public void handleBuild(News news) throws NoMoreMovesException {
        String s = "BUILDKO";
        try{
            if (gameTable.getCurrentBuilder() != null && news.getBuilder(gameTable) != gameTable.getCurrentBuilder())
                throw new InvalidBuildException(); /* trying to build using the builder which I didn't previously move */
           // gameTable.checkConditions();
            news.getCell(gameTable).setHeight(news.getBuilder(gameTable), news.getHeight());
            gameTable.getCurrentPlayer().setState(WAIT);
            gameTable.nextTurn();
            s = "TURN";
        } catch (InvalidBuildException ignored) {
        } catch (DemeterException | HephaestusException e) {
            gameTable.getCurrentPlayer().setState(BUILDORPASS);
            gameTable.getCurrentPlayer().setFirstTime(false);
            s = "BUILDOK";
        } catch (PrometheusException e){
            gameTable.getCurrentPlayer().setState(MOVE);
            s = "BUILDOK";
        }
        finally {
            if (s.equals("BUILDOK") && gameTable.getCurrentBuilder() == null) {
                gameTable.setCurrentBuilder(news.getBuilder(gameTable));
                gameTable.checkConditions();
            } else if (s.equals("BUILDKO")) news.setRecipients(news.getSender().getPlayer());
            gameTable.setNews(news, s);
        }
    }

    /**
     * Constructor for the BuildController.Takes gameTable as argument.
     */
    public BuildController(GameTable gameTable){
        this.gameTable = gameTable;
    }
}
