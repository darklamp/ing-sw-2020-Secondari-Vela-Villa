package it.polimi.ingsw;

import it.polimi.ingsw.Model.BuildingType;
import it.polimi.ingsw.Model.Cell;

import java.lang.reflect.Field;

public class TestUtilities {
    private final static Field f;

    static {
        Field f1;
        try {
            f1 = Cell.class.getDeclaredField("height");
        } catch (NoSuchFieldException e) {
            f1 = null;
        }
        f = f1;
        f.setAccessible(true);
    }

    public static void mustSetHeight(Cell cell, BuildingType height) {
        try {
            f.set(cell, height);
        } catch (IllegalAccessException ignored) {
        }
    }
}
