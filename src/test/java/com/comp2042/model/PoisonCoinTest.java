package com.comp2042.model;

import org.junit.jupiter.api.Test;
import java.awt.Point;
import static org.junit.jupiter.api.Assertions.*;

class PoisonCoinTest {
    @Test void pickupShortensByTwoWithoutAddingScore() {
        Game game = new Game(12, 12, true, 'N');
        GameSession session = new GameSession(game);
        Snake snake = session.snake();
        snake.move('U');
        snake.move('U');
        snake.move('U');
        int before = snake.getBody().size();
        Point next = new Point(snake.getHead().x, snake.getHead().y - 1);
        PoisonCoin poison = new PoisonCoin(next);
        session.replaceCoin(poison);
        snake.move('U');
        assertTrue(snake.ateCoin(poison));
        session.collectCoin();
        assertEquals(before - 2, snake.getBody().size());
        assertEquals(0, game.getScore());
        assertEquals(0, game.getScorePoints());
    }

    @Test void shortSnakeKeepsItsHead() {
        GameSession session = new GameSession(new Game(8, 8, true, 'N'));
        Snake snake = session.snake();
        snake.shrink(100);
        assertTrue(snake.getBody().isEmpty());
        Point head = snake.getHead();
        session.replaceCoin(new PoisonCoin(head));
        session.collectCoin();
        assertEquals(head, snake.getHead());
        assertFalse(snake.getDead());
        assertThrows(IllegalArgumentException.class, () -> snake.shrink(-1));
    }
}
