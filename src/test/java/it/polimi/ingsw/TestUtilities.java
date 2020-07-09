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

package it.polimi.ingsw;

import it.polimi.ingsw.Model.BuildingType;
import it.polimi.ingsw.Model.Cell;
import it.polimi.ingsw.Network.ServerConf;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import java.lang.reflect.Field;

@SuppressWarnings("ALL")
public class TestUtilities {
    private final static Field height;

    static {
        Field f1;
        try {
            f1 = Cell.class.getDeclaredField("height");
        } catch (NoSuchFieldException e) {
            f1 = null;
        }
        height = f1;
        height.setAccessible(true);
    }

    public static void mustSetHeight(Cell cell, BuildingType height) {
        try {
            TestUtilities.height.set(cell, height);
        } catch (IllegalAccessException ignored) {
        }
    }

    public static void enablePersistence() throws Exception {
        ServerConf serverConf = getAccessibleField("serverConf", ServerMain.class);
        serverConf.disk = true;
    }

    public static void disablePersistence() throws Exception {
        ServerConf serverConf = getAccessibleField("serverConf", ServerMain.class);
        serverConf.disk = false;
    }

    public static void enableDebugVerbosity() {
        Configurator.setRootLevel(Level.DEBUG);
    }

    public static void disableDebugVerbosity() {
        Configurator.setRootLevel(Level.ERROR);

    }

    public static <T> T getAccessibleField(String s, Object c) {
        try {
            Field out = c.getClass().getDeclaredField(s);
            out.setAccessible(true);
            Class<?> clas = out.get(c).getClass();
            return (T) clas.cast(out.get(c));
        } catch (Exception ignored) {
        }
        return null;
    }

    public static <T> T getAccessibleField(String s, Class<?> c) {
        try {
            Field out = c.getDeclaredField(s);
            out.setAccessible(true);
            Class<?> clas = out.get(null).getClass();
            return (T) clas.cast(out.get(null));
        } catch (Exception ignored) {
        }
        return null;
    }
}
