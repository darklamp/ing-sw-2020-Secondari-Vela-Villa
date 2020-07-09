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

import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.ServerMain;

import java.io.Serializable;
import java.util.List;

/**
 * Holds the server's MOTD, the available gods as a list, and a flag to tell clients if persistence is enabled.
 */
public class MOTD implements Serializable, Message {

    private static final long serialVersionUID = 17756L;

    private final String content;

    private final List<String> gods = GameTable.completeGodList;

    private final boolean persistence = ServerMain.persistence();

    public boolean persistenceEnabled() {
        return persistence;
    }

    public String getMOTD() {
        return content;
    }

    public List<String> getGods() {
        return gods;
    }

    public MOTD(String string) {
        content = string;
    }

}
