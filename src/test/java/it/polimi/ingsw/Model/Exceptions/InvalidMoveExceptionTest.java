package it.polimi.ingsw.Model.Exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InvalidMoveExceptionTest {
    @Test
    void constructorTest() {
        Exception e = new InvalidMoveException("ASD");
        assertEquals(e.getMessage(), "ASD");
    }
}