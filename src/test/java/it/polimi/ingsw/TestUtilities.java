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
