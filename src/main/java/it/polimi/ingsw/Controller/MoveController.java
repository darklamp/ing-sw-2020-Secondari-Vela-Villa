/*
 * Santorini
 * Copyright (C)  2020  Alessandro Villa and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Affero General Public License as
 *      published by the Free Software Foundation, either version 3 of the
 *      License, or (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Affero General Public License for more details.
 *
 *      You should have received a copy of the GNU Affero General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * E-mail contact addresses:
 * darklampz@gmail.com
 * alessandro17.villa@mail.polimi.it
 *
 */

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
