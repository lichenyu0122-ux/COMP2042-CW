package com.comp2042;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameDifficultyTest {

    @Test
    void easyDifficultyHasExpectedSettings() {
        Game game = new Game(20, 20, false, 'E');

        assertEquals(8.5, game.getSpeed());
        assertTrue(game.isNoBorder());
    }

    @Test
    void normalDifficultyHasExpectedSettings() {
        Game game = new Game(20, 20, false, 'N');

        assertEquals(8.5, game.getSpeed());
        assertFalse(game.isNoBorder());
    }

    @Test
    void hardDifficultyHasExpectedSettings() {
        Game game = new Game(20, 20, false, 'H');

        assertEquals(13.0, game.getSpeed());
        assertFalse(game.isNoBorder());
    }

    @Test
    void unsupportedDifficultyIsRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Game(20, 20, false, 'X')
        );
    }
}