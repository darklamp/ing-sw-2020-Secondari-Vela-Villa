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
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;

import static it.polimi.ingsw.Client.ClientState.*;

@God(name = "PAN")
public class Pan extends Builder{

    Pan(Cell position, Player player) {
        super(position, player);
    }

    protected ClientState isValidBuild(Cell cell, BuildingType newheight) throws InvalidBuildException {
        super.isValidBuild(cell, newheight);
        verifyBuild(cell, newheight);
        return WAIT;
    }


    @Override
    protected void isValidMove(Cell finalPoint) throws InvalidMoveException {
        super.isValidMove(finalPoint);
        verifyMove(finalPoint);
    }

    @Override
    protected ClientState executeMove(Cell position) {
        ClientState out = this.getPosition().getHeight().compareTo(position.getHeight()) >= 2 ? WIN : BUILD;
        if (out == WIN) return WIN;
        else {
            super.executeMove(position);
            return BUILD;
        }
    }
}
