package com.comp2042.model;

import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObstacleTest {
    @Test
    void newObstaclesUseDistinctFreeCellsInsideGrid() {
        Obstacle obstacle = new Obstacle(5, 5);
        Snake snake = new Snake(5, 5, false, obstacle);
        Coin coin = new Coin(5, 5, snake, obstacle);

        obstacle.newObstacles(snake, coin, 25);
        List<Point> positions = obstacle.getObstacles();

        assertEquals(22, positions.size());
        assertEquals(positions.size(), new HashSet<>(positions).size());
        for (Point point : positions) {
            assertTrue(point.x >= 0 && point.x < 5);
            assertTrue(point.y >= 0 && point.y < 5);
            assertFalse(point.equals(snake.getHead()));
            assertFalse(snake.getBody().contains(point));
            assertFalse(point.equals(coin.getPos()));
        }

        obstacle.reset();
        assertTrue(obstacle.getObstacles().isEmpty());
    }

    @Test
    void obstacleCountNeverExceedsFifty() {
        Obstacle obstacle = new Obstacle(12, 12);
        Snake snake = new Snake(12, 12, false, obstacle);
        Coin coin = new Coin(12, 12, snake, obstacle);

        obstacle.newObstacles(snake, coin, 50);
        obstacle.newObstacles(snake, coin, 1);

        assertEquals(50, obstacle.getObstacles().size());
    }
}
