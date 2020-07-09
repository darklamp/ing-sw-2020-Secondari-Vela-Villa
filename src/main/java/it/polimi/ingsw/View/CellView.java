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

package it.polimi.ingsw.View;

import it.polimi.ingsw.Model.Builder;
import it.polimi.ingsw.Model.BuildingType;

import java.io.Serializable;

/**
 * Representation of a {@link it.polimi.ingsw.Model.Cell} sent by the server to clients.
 */
public class CellView implements Serializable {

    private static final long serialVersionUID = 17756L;
    int player;
    /**
     * True if the builder on the cell is the first.
     * {@link Builder#isFirst()}
     */
    boolean first;
    BuildingType height;

    public int getPlayer() {
        return player;
    }

    public boolean isFirst() {
        return first;
    }

    public BuildingType getHeight() {
        return height;
    }

    public CellView(BuildingType height, int player, boolean isFirst){
        this.height = height;
        this.player = player;
        this.first = isFirst;
    }

}
