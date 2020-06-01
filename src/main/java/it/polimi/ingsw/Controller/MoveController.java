package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Model.Cell;
import it.polimi.ingsw.Model.Exceptions.*;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Model.News;

import static it.polimi.ingsw.Client.ClientState.BUILD;
import static it.polimi.ingsw.Client.ClientState.MOVEORBUILD;

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
        String moveResult = "MOVEKO";
        boolean winner = false;
        try {
            if (gameTable.getCurrentBuilder() != null) {
                if (news.getBuilder(gameTable) != gameTable.getCurrentBuilder()) throw new InvalidMoveException();
            }
            gameTable.checkMovePreConditions();
            news.getBuilder(gameTable).setPosition(news.getCell(gameTable));
            gameTable.getCurrentPlayer().setState(BUILD);
            if (gameTable.getCurrentBuilder() == null) {
                gameTable.setCurrentBuilder(news.getBuilder(gameTable));
            }
            moveResult = "MOVEOK";
        } catch (InvalidMoveException ignored) { /* finally executes */
        } catch (PanException e) {
            winner = true;
            throw new WinnerException(gameTable.getCurrentPlayer());
        } catch (WinnerException e) {
            winner = true;
            throw e;
        } catch (ArtemisException e) {
            gameTable.getCurrentPlayer().setState(MOVEORBUILD);
            gameTable.setCurrentBuilder(news.getBuilder(gameTable));
            moveResult = "MOVEOK";
        } catch (MinotaurException e) {
            Cell cellBehind = null, cellOnWhichMinotaurIsToBeMoved;
            news.getBuilder(gameTable).getPosition().setBuilder(null);
            try {
                cellBehind = gameTable.getCell(e.getPair().getFirst(), e.getPair().getSecond());
            } catch (InvalidCoordinateException ignored) {
            }
            cellOnWhichMinotaurIsToBeMoved = news.getCell(gameTable);
            cellOnWhichMinotaurIsToBeMoved.getBuilder().forceMove(cellBehind);
            news.getBuilder(gameTable).forceMove(cellOnWhichMinotaurIsToBeMoved);
            if (news.getBuilder(gameTable).isWinner()) throw new WinnerException(gameTable.getCurrentPlayer());
            gameTable.getCurrentPlayer().setState(BUILD);
            gameTable.setCurrentBuilder(news.getBuilder(gameTable));
            moveResult = "MOVEOK";
        }
        finally {
            if (moveResult.equals("MOVEKO")) news.setRecipients(news.getSender().getPlayer());
            if (!winner) gameTable.setNews(news, moveResult);
        }

    }



}
