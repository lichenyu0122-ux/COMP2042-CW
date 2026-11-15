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
        session.replaceCoin(new Coin(new Point(0, 0)));
        session.placePoisonCoin(new PoisonCoin(next));
        session.move('U');
        assertEquals(GameSession.Pickup.POISON, session.finishMove());
        assertEquals(before - 2, snake.getBody().size());
        assertEquals(0, game.getScore());
        assertEquals(0, game.getScorePoints());
        assertNotNull(session.coin());
        assertNull(session.poisonCoin());
    }

    @Test void shortSnakeKeepsItsHead() {
        GameSession session = new GameSession(new Game(8, 8, true, 'N'));
        Snake snake = session.snake();
        snake.shrink(100);
        assertTrue(snake.getBody().isEmpty());
        Point next = new Point(snake.getHead().x - 1, snake.getHead().y);
        session.replaceCoin(new Coin(new Point(0, 0)));
        session.placePoisonCoin(new PoisonCoin(next));
        session.move('L');
        assertEquals(GameSession.Pickup.POISON, session.finishMove());
        assertEquals(next, snake.getHead());
        assertFalse(snake.getDead());
        assertTrue(snake.getBody().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> snake.shrink(-1));
    }

    @Test void rewardRemainsCollectibleWhilePoisonIsPresent() {
        GameSession session = new GameSession(new Game(8, 8, true, 'N'));
        session.replaceCoin(new Coin(new Point(3, 4)));
        session.placePoisonCoin(new PoisonCoin(new Point(0, 0)));
        session.move('L');
        assertEquals(GameSession.Pickup.REWARD, session.finishMove());
        assertEquals(1, session.game().getScore());
        assertNotNull(session.poisonCoin());
        session.reset();
        assertNull(session.poisonCoin());
    }

    @Test void rewardAndPoisonNeverShareACell() {
        GameSession session = new GameSession(new Game(8, 8, true, 'N'));
        session.replaceCoin(new Coin(new Point(0, 0)));
        assertThrows(IllegalArgumentException.class,
                () -> session.placePoisonCoin(new PoisonCoin(new Point(0, 0))));
        session.placePoisonCoin(new PoisonCoin(new Point(1, 1)));
        session.replaceCoin(new Coin(new Point(1, 1)));
        assertNotEquals(session.coin().getPos(), session.poisonCoin().getPos());
    }
}
