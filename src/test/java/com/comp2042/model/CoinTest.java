package com.comp2042.model;

import org.junit.jupiter.api.Test;
import java.awt.Point;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CoinTest {
    @Test void noFreeCellFailsInsteadOfLoopingForever() {
        Obstacle obstacle = new Obstacle(3, 3);
        Snake snake = new Snake(3, 3, false, obstacle);
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                Point cell = new Point(x, y);
                if (!cell.equals(snake.getHead()) && !snake.getBody().contains(cell)) {
                    obstacle.getObstacles().add(cell);
                }
            }
        }
        assertThrows(IllegalStateException.class, () -> new Coin(3, 3, snake, obstacle));
    }
}
