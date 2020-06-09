package it.polimi.ingsw.Model;

import org.junit.jupiter.api.Test;

import static it.polimi.ingsw.Model.BuildingType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BuildingTypeTest {

    @Test
    void getNextTest() throws Exception {
        BuildingType b = NONE;
        assertEquals(b.getNext(), BASE);
        b = BASE;
        assertEquals(b.getNext(), MIDDLE);
        b = MIDDLE;
        assertEquals(b.getNext(), TOP);
        b = TOP;
        assertEquals(b.getNext(), DOME);
        b = DOME;

        BuildingType finalB = b;
        assertThrows(IllegalArgumentException.class, finalB::getNext);
    }

    @Test
    void parse() {
        int i = 1;
        assertEquals(BuildingType.parse(i), MIDDLE);
        i = 2;
        assertEquals(BuildingType.parse(i), TOP);
        i = 3;
        assertEquals(BuildingType.parse(i), DOME);
        i = -1;
        assertEquals(BuildingType.parse(i), BASE);

    }
}