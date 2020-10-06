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

/**
 * Colors for CLI.
 */
public enum Color {
    ANSI_RED("\u001B[31m"),
    ANSI_GREEN("\u001B[32m"),
    ANSI_YELLOW("\u001B[33m"),
    ANSI_BLUE("\u001B[34m"),
    ANSI_PURPLE("\u001B[35m"),
    ANSI_CYAN("\u001B[36m"),
    ANSI_WHITE("\u001B[37m");

    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001b[1m";

    private final String escape;

    Color(String escape) {
        this.escape = escape;
    }

    public Color next() {
        if (this.ordinal() == Color.values().length - 1) return Color.values()[0];
        else return Color.values()[this.ordinal() + 1];
    }

    public static String Rainbow(String s) {
        StringBuilder out = new StringBuilder();
        Color cur = ANSI_RED;
        int count = 0;
        for (char c : s.toCharArray()) {
            if (c != ' ') {
                out.append(cur).append(c);
                if (count == 3) {
                    cur = cur.next();
                    count = 0;
                } else count += 1;
            } else out.append(' ');
        }
        out.append(Color.RESET);
        return out.toString();
    }

    @Override
    public String toString() {
        return escape;
    }
}
