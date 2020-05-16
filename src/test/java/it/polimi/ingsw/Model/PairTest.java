package it.polimi.ingsw.Model;

import it.polimi.ingsw.Utility.Pair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PairTest {

    @Test
    void testEquals() {
        Pair p1 = new Pair(2, 1);
        Pair p2 = new Pair(1, 2);
        Pair p3 = new Pair(0, -1);
        Pair p4 = new Pair(2, 1);

        assertEquals(p1, p4);
        assertNotEquals(p1, p2);
        assertNotEquals(p1, p3);

    }
}