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
     * @param news contains the cell where the player wants to move.
     * @throws WinnerException
     * also catch gods' exceptions about moving powers
     */
    void handleMove(News news) throws WinnerException {
        String moveResult = "MOVEKO";
        try{
            if (gameTable.getCurrentBuilder() != null) {
                if (news.getBuilder(gameTable) != gameTable.getCurrentBuilder()) throw new InvalidMoveException();
            }
            news.getBuilder(gameTable).setPosition(news.getCell(gameTable));
            gameTable.getCurrentPlayer().setState(BUILD);
            if (gameTable.getCurrentBuilder() == null) {
                gameTable.setCurrentBuilder(news.getBuilder(gameTable));
            }
            moveResult = "MOVEOK";
        }
        catch (InvalidMoveException ignored){ /* finally executes */
        }
        catch (PanException e){
            throw new WinnerException(gameTable.getCurrentPlayer());
        }
        catch (ArtemisException e){
            gameTable.getCurrentPlayer().setState(MOVEORBUILD);
            gameTable.setCurrentBuilder(news.getBuilder(gameTable));
            moveResult = "MOVEOK";
        }
        catch (MinotaurException e){
            Cell cellBehind, cellOnWhichMinotaurIsToBeMoved;
            try {
                news.getBuilder(gameTable).getPosition().setBuilder(null);
                cellBehind = gameTable.getCell(e.getPair().getFirst(),e.getPair().getSecond());
                cellOnWhichMinotaurIsToBeMoved = news.getCell(gameTable);
                cellOnWhichMinotaurIsToBeMoved.getBuilder().forceMove(cellBehind);
                news.getBuilder(gameTable).forceMove(cellOnWhichMinotaurIsToBeMoved);
                if (news.getBuilder(gameTable).isWinner()) throw new WinnerException(gameTable.getCurrentPlayer());
                gameTable.getCurrentPlayer().setState(BUILD);
                gameTable.setCurrentBuilder(news.getBuilder(gameTable));
                moveResult = "MOVEOK";
            } catch (Exception ee) {
                moveResult = "MOVEKO";
            }
        }
        finally {
            gameTable.setNews(news,moveResult);
        }

    }



}
