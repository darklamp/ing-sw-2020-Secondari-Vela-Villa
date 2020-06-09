package it.polimi.ingsw;

import it.polimi.ingsw.Model.BuildingType;
import it.polimi.ingsw.Model.Cell;

import java.lang.reflect.Field;

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
}
