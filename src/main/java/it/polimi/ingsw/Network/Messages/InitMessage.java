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

package it.polimi.ingsw.Network.Messages;

import it.polimi.ingsw.Network.Server;

import java.io.Serializable;

public class InitMessage implements Serializable, Message {

    private static final long serialVersionUID = 17756L;

    private final int pIndex, size, gameIndex, god1, god2, god3;

    private final long moveTimer;

    public InitMessage(int pIndex, int size, int gameIndex, int god1, int god2, int god3) {
        this.pIndex = pIndex;
        this.size = size;
        this.gameIndex = gameIndex;
        this.god1 = god1;
        this.god2 = god2;
        this.god3 = god3;
        this.moveTimer = Server.getMoveTimer();
    }
    public long getMoveTimer() {
        return moveTimer;
    }

    public int getPlayerIndex() {
        return pIndex;
    }

    public int getSize() {
        return size;
    }

    public int getGameIndex() {
        return gameIndex;
    }

    public int getGod(int i) {
        return switch (i) {
            case 2 -> god3;
            case 1 -> god2;
            default -> god1;
        };
    }
}
