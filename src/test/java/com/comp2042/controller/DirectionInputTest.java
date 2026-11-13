package com.comp2042.controller;

import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DirectionInputTest {
    @Test
    void oppositeAndRepeatedDirectionsAreRejected() {
        assertFalse(DirectionInput.isValidDirection(KeyCode.LEFT, KeyCode.RIGHT));
        assertFalse(DirectionInput.isValidDirection(KeyCode.RIGHT, KeyCode.LEFT));
        assertFalse(DirectionInput.isValidDirection(KeyCode.UP, KeyCode.DOWN));
        assertFalse(DirectionInput.isValidDirection(KeyCode.DOWN, KeyCode.UP));
        assertFalse(DirectionInput.isValidDirection(KeyCode.LEFT, KeyCode.LEFT));
    }

    @Test
    void perpendicularDirectionsAreAccepted() {
        assertTrue(DirectionInput.isValidDirection(KeyCode.LEFT, KeyCode.UP));
        assertTrue(DirectionInput.isValidDirection(KeyCode.UP, KeyCode.RIGHT));
        assertTrue(DirectionInput.isValidDirection(KeyCode.RIGHT, KeyCode.DOWN));
        assertTrue(DirectionInput.isValidDirection(KeyCode.DOWN, KeyCode.LEFT));
    }

    @Test
    void nonArrowKeysAreRejected() {
        assertFalse(DirectionInput.isValidDirection(KeyCode.UP, KeyCode.P));
        assertFalse(DirectionInput.isValidDirection(KeyCode.UP, null));
    }

    @Test
    void onlyOneTurnIsAcceptedPerTick() {
        DirectionInput input = new DirectionInput();
        assertTrue(input.tryTurn(KeyCode.UP));
        assertFalse(input.tryTurn(KeyCode.RIGHT));
        assertTrue(input.current() == KeyCode.UP);

        input.nextTick();
        assertTrue(input.tryTurn(KeyCode.RIGHT));
        assertTrue(input.current() == KeyCode.RIGHT);
    }

    @Test
    void resetRestoresInitialDirectionAndTurnAllowance() {
        DirectionInput input = new DirectionInput();
        assertTrue(input.tryTurn(KeyCode.UP));
        input.reset();

        assertTrue(input.current() == KeyCode.LEFT);
        assertTrue(input.tryTurn(KeyCode.DOWN));
    }
}
