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

package it.polimi.ingsw.Network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Server configuration helper class.
 * NB: the values here are the defaults. They can get overridden by the yaml conf,
 * and the yaml conf can in turn be overridden by command line arguments.
 */
public class ServerConf {
    public String ip = "0.0.0.0";
    public int port = 1337;
    public boolean disk = false;
    public String verb = "info";
    public short moveTimer = 2;
    public boolean console = false;
    public String MOTD = "Have fun!";
    public String timeunit = "m";
    public int maxPlayers = 3;
    public List<String> gods = new ArrayList<>(Arrays.asList("APOLLO", "ARTEMIS", "ATHENA", "ATLAS", "DEMETER", "HEPHAESTUS", "MINOTAUR", "PAN", "PROMETHEUS"));
}
