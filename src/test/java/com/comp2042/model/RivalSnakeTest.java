package com.comp2042.model;

import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RivalSnakeTest {
    @Test
    void reverseTurnIsRejected() {
        RivalSnake snake = new RivalSnake(20, 20, new Point(5, 5), Heading.RIGHT);

        assertFalse(snake.turnTo(Heading.LEFT));
        assertEquals(Heading.RIGHT, snake.heading());
        assertTrue(snake.turnTo(Heading.UP));
    }

    @Test
    void bluePowerWrapsAcrossTheBorderForLimitedMoves() {
        RivalSnake snake = new RivalSnake(20, 20, new Point(0, 5), Heading.LEFT);
        assertFalse(snake.move(false, Set.of()));

        snake.giveWrap();
        assertTrue(snake.move(false, Set.of()));
        assertEquals(new Point(19, 5), snake.head());
        for (int move = 1; move < 16; move++) {
            assertTrue(snake.move(false, Set.of()));
        }
        assertFalse(snake.canWrap());
    }

    @Test
    void rainbowSpeedBoostExpiresAfterTwelveMoves() {
        RivalSnake snake = new RivalSnake(20, 20, new Point(2, 5), Heading.RIGHT);
        snake.giveSpeedBoost();

        for (int move = 0; move < 12; move++) {
            assertTrue(snake.speedBoosted());
            assertTrue(snake.move(false, Set.of()));
        }
        assertFalse(snake.speedBoosted());
    }
}
