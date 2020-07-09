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

package it.polimi.ingsw.Model;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;
import it.polimi.ingsw.Utility.Pair;

import static it.polimi.ingsw.Client.ClientState.BUILD;
import static it.polimi.ingsw.Client.ClientState.WAIT;

@God(name = "MINOTAUR")
public class Minotaur extends Builder {
    Minotaur(Cell position, Player player) {
        super(position, player);
    }


    @Override
    protected ClientState isValidBuild(Cell cell, BuildingType newheight) throws InvalidBuildException {
        super.isValidBuild(cell, newheight);
        verifyBuild(cell, newheight);
        return WAIT;
    }


    @Override
    protected void isValidMove(Cell finalPoint) throws InvalidMoveException {
        super.isValidMove(finalPoint);
        if (finalPoint.getBuilder() != null) { // there's a builder on the cell I'm trying to move to
            if (!(checkEmptyCellBehind(finalPoint) && finalPoint.getBuilder().getPlayer() != this.getPlayer()))
                throw new InvalidMoveException();
        } else super.verifyMove(finalPoint);
    }

    /**
     * Checks whether the {@link Cell} behind the given one is empty
     *
     * @param finalPoint cell on which builder wants to position
     * @return true if the cell behind the occupied one is empty and valid; else false
     * @throws InvalidMoveException if the cell is invalid
     */
    boolean checkEmptyCellBehind(Cell finalPoint) throws InvalidMoveException {
        int diffY = finalPoint.getRow() - this.getPosition().getRow();
        int diffX = finalPoint.getColumn() - this.getPosition().getColumn();
        try {
            if (diffY == 1) {
                if (diffX == 1) {
                    return finalPoint.getRow() != 4 && finalPoint.getColumn() != 4 && finalPoint.movableCell(finalPoint.getRow() + 1, finalPoint.getColumn() + 1); //OK
                } else if (diffX == -1) {
                    return finalPoint.getRow() != 4 && finalPoint.getColumn() != 0 && finalPoint.movableCell(finalPoint.getRow() + 1, finalPoint.getColumn() - 1); //OK
                } else {
                    return finalPoint.getRow() != 4 && finalPoint.movableCell(finalPoint.getRow() + 1, finalPoint.getColumn()); //OK
                }
            } else if (diffY == -1) {
                if (diffX == 1) {
                    return finalPoint.getRow() != 0 && finalPoint.getColumn() != 4 && finalPoint.movableCell(finalPoint.getRow() - 1, finalPoint.getColumn() + 1); //OK
                } else if (diffX == -1) {
                    return finalPoint.getRow() != 0 && finalPoint.getColumn() != 0 && finalPoint.movableCell(finalPoint.getRow() - 1, finalPoint.getColumn() - 1); //OK
                } else {
                    return finalPoint.getRow() != 0 && finalPoint.movableCell(finalPoint.getRow() - 1, finalPoint.getColumn()); //OK
                }
            } else {
                if (diffX == 1) {
                    return finalPoint.getColumn() != 4 && finalPoint.movableCell(finalPoint.getRow(), finalPoint.getColumn() + 1); //OK
                } else if (diffX == -1) {
                    return finalPoint.getColumn() != 0 && finalPoint.movableCell(finalPoint.getRow(), finalPoint.getColumn() - 1); //OK
                }
                return false;
            }
        } catch (InvalidCoordinateException e) {
            throw new InvalidMoveException();
        }

    }

    /**
     * Gets, if possible, the coordinates of the cell behind the given one.
     *
     * @param finalPoint is the cell where the Minotaur builder wants to move and it's already occupied.
     * @return the coordinates where the builder from finalPoint is forced to move.
     */
    Pair getCellBehind(Cell finalPoint) {
        int diffY = finalPoint.getRow() - this.getPosition().getRow();
        int diffX = finalPoint.getColumn() - this.getPosition().getColumn();
        if (diffY == 1) {
            if (diffX == 1) {
                return new Pair(finalPoint.getRow() + 1, finalPoint.getColumn() + 1);
            } else if (diffX == -1) {
                return new Pair(finalPoint.getRow() + 1, finalPoint.getColumn() - 1);
            } else {
                return new Pair(finalPoint.getRow() + 1, finalPoint.getColumn());
            }
        } else if (diffY == -1){
            if (diffX == 1) {
                return new Pair(finalPoint.getRow() - 1, finalPoint.getColumn() + 1);
            } else if (diffX == -1) {
                return new Pair(finalPoint.getRow() - 1, finalPoint.getColumn() - 1);
            } else {
                return new Pair(finalPoint.getRow() - 1, finalPoint.getColumn());
            }
        } else {
            if (diffX == 1) {
                return new Pair(finalPoint.getRow(), finalPoint.getColumn() + 1);
            } else if (diffX == -1) {
                return new Pair(finalPoint.getRow(), finalPoint.getColumn() - 1);
            }
        }
        return new Pair(0, 0);
    }

    @Override
    protected ClientState executeMove(Cell position) {
        if (position.getBuilder() != null) { // there's a builder on the cell I'm trying to move to
            Cell cellBehind = null;
            this.getPosition().setBuilder(null);
            Pair cb = getCellBehind(position);
            try {
                cellBehind = this.getPosition().getGameTable().getCell(cb.getFirst(), cb.getSecond());
            } catch (InvalidCoordinateException ignored) {
            }
            assert cellBehind != null;
            position.getBuilder().forceMove(cellBehind);
            this.forceMove(position);
        } else super.executeMove(position);
        return BUILD;
    }
}
