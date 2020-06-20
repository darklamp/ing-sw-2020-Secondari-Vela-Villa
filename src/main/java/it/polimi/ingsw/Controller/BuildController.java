package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.NoMoreMovesException;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.News;

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
        try{
            if (gameTable.getCurrentBuilder() != null && news.getBuilder(gameTable) != gameTable.getCurrentBuilder())
                throw new InvalidBuildException(); /* trying to build using the builder which I didn't previously move */
            news.getCell(gameTable).setHeight(news.getBuilder(gameTable), news.getHeight());
        } catch (InvalidBuildException ignored) {
        }
    }

    /**
     * Constructor for the BuildController.Takes gameTable as argument.
     *
     * @param gameTable table to be referenced
     */
    public BuildController(GameTable gameTable){
        this.gameTable = gameTable;
    }
}
