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

package it.polimi.ingsw.Utility;

import java.io.Serializable;
import java.util.Objects;

/**
 * Simple class to handle a pair of ints.
 */
public class Pair implements Serializable {

    private static final long serialVersionUID = 17756L;

    public int getFirst() {
        return num1;
    }

    public int getSecond() {
        return num2;
    }

    private final int num1, num2;

    public Pair(int num1, int num2)
    {
        this.num1 = num1;
        this.num2 = num2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return num1 == pair.num1 &&
                num2 == pair.num2;
    }

    @Override
    public int hashCode() {
        return Objects.hash(num1, num2);
    }

}
