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

package it.polimi.ingsw.Model.Exceptions;

import it.polimi.ingsw.Model.Player;

/**
 * Thrown when a player has no feasible moves/builds left.
 */
public class NoMoreMovesException extends  Exception{
    private Player player;
    private NoMoreMovesException(){
        super();
    }
    public NoMoreMovesException(Player p){
        this();
        this.player = p;
    }

    public Player getPlayer() {
        return player;
    }
}
