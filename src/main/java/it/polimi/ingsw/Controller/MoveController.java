package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Model.Builder;
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

    void handleMove(News news) throws WinnerException {
        String moveResult = "MOVEKO";
        try{
            news.getBuilder(gameTable).setPosition(news.getCell(gameTable));
            gameTable.getCurrentPlayer().setState(BUILD);
            gameTable.setCurrentBuilder(news.getBuilder(gameTable));
            moveResult = "MOVEOK";
        }
        catch (PanException e){
//TODO 1
        }
        catch (ArtemisException e){
            gameTable.getCurrentPlayer().setState(MOVEORBUILD);
            gameTable.setCurrentBuilder(news.getBuilder(gameTable));
            moveResult = "MOVEOK";
        }
        catch (PrometeusException e) {
            //TODO
        }
        catch (InvalidMoveException ignored){ /* finally executes */
        }
        catch (MinotaurException e){
            Cell cellBehind, cellOnWhichMinotaurIsToBeMoved;
            try {
                cellBehind = gameTable.getCell(e.getPair().getFirst(),e.getPair().getSecond());
                cellOnWhichMinotaurIsToBeMoved = news.getCell(gameTable);
                cellBehind.getBuilder().forceMove(cellBehind);
                news.getBuilder(gameTable).forceMove(cellOnWhichMinotaurIsToBeMoved);
            } catch (InvalidCoordinateException ignored) {
            }
            if (news.getBuilder(gameTable).isWinner()) throw new WinnerException(gameTable.getCurrentPlayer());
            moveResult = "MOVEOK";
        }
        finally {
            gameTable.setNews(news,moveResult);
        }

    }



}
