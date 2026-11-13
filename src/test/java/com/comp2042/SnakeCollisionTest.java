package com.comp2042;

import org.junit.jupiter.api.Test;

import java.awt.Point;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SnakeCollisionTest {
    @Test
    void hittingWallEndsGameWithoutMovingHeadOutsideGrid() {
        Snake snake = new Snake(4, 4, false, new Obstacle(4, 4));

        snake.move('U');
        snake.move('U');
        snake.move('U');

        assertTrue(snake.getDead());
        assertEquals(new Point(2, 0), snake.getHead());
    }

    @Test
    void hittingObstacleEndsGame() {
        Obstacle obstacle = new Obstacle(8, 8);
        obstacle.getObstacles().add(new Point(4, 3));
        Snake snake = new Snake(8, 8, false, obstacle);

        snake.move('U');

        assertTrue(snake.getDead());
        assertEquals(new Point(4, 4), snake.getHead());
    }

    @Test
    void movingOntoBodyEndsGame() {
        Snake snake = new Snake(12, 12, false, new Obstacle(12, 12));

        snake.move('U');
        snake.move('L');
        snake.move('D');
        assertFalse(snake.getDead());
        snake.move('R');

        assertTrue(snake.hitSelf());
        assertTrue(snake.getDead());
    }
}
