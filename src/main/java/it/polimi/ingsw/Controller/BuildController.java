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
