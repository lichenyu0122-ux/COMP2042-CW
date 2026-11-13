package com.comp2042;

import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameControllerDirectionTest {
    @Test
    void oppositeAndRepeatedDirectionsAreRejected() {
        assertFalse(GameController.isValidDirection(KeyCode.LEFT, KeyCode.RIGHT));
        assertFalse(GameController.isValidDirection(KeyCode.RIGHT, KeyCode.LEFT));
        assertFalse(GameController.isValidDirection(KeyCode.UP, KeyCode.DOWN));
        assertFalse(GameController.isValidDirection(KeyCode.DOWN, KeyCode.UP));
        assertFalse(GameController.isValidDirection(KeyCode.LEFT, KeyCode.LEFT));
    }

    @Test
    void perpendicularDirectionsAreAccepted() {
        assertTrue(GameController.isValidDirection(KeyCode.LEFT, KeyCode.UP));
        assertTrue(GameController.isValidDirection(KeyCode.UP, KeyCode.RIGHT));
        assertTrue(GameController.isValidDirection(KeyCode.RIGHT, KeyCode.DOWN));
        assertTrue(GameController.isValidDirection(KeyCode.DOWN, KeyCode.LEFT));
    }

    @Test
    void NonArrowKeysAreRejected() {
        assertFalse(GameController.isValidDirection(KeyCode.UP, KeyCode.P));
        assertFalse(GameController.isValidDirection(KeyCode.UP, null));
    }
}
