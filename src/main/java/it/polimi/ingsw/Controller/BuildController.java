package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Model.Exceptions.DemeterException;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.Move;

public class BuildController {

    public final GameTable gameTable;

    public BuildController(GameTable g) {
        this.gameTable = g;
    }

    public void handleBuild(Move move) throws InvalidBuildException, DemeterException {
        move.getCell().setHeight(move.getBuilder(), move.getHeight());
        gameTable.setNews(move,"BUILD");
    }
}
