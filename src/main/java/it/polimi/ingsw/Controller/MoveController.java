package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;
import it.polimi.ingsw.Model.Exceptions.NoMoreMovesException;
import it.polimi.ingsw.Model.Exceptions.WinnerException;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.News;

/**
 * Controller responsible for handling move
 */
public class MoveController {
    private final GameTable gameTable;

    public MoveController(GameTable gameTable){
        this.gameTable = gameTable;
    }

    /**
     * Method responsible for handling a "Move" news.
     *
     * @param news contains the cell where the player wants to move.
     * @throws WinnerException      when a winner is found.
     * @throws NoMoreMovesException when a player is found to have no more feasible moves.
     */
    void handleMove(News news) throws WinnerException, NoMoreMovesException {
        boolean winner = false, invalidMove = false;
        try {
            if (gameTable.getCurrentBuilder() != null) {
                if (news.getBuilder(gameTable) != gameTable.getCurrentBuilder()) throw new InvalidMoveException();
            }
            news.getBuilder(gameTable).setPosition(news.getCell(gameTable));
        } catch (InvalidMoveException e) {
            invalidMove = true;
        } catch (WinnerException e) {
            winner = true;
            throw e;
        } finally {
            if (!invalidMove && !winner) gameTable.checkConditions();
        }

    }



}
