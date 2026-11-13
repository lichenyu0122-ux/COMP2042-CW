package com.comp2042.model;

import org.junit.jupiter.api.Test;

import java.awt.Point;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameSessionTest {
    @Test
    void resetRebuildsRoundWithoutOldScoreOrObstacles() {
        Game game = new Game(8, 8, false, 'N');
        GameSession session = new GameSession(game);
        Snake oldSnake = session.snake();
        Coin oldCoin = session.coin();
        session.obstacle().getObstacles().add(new Point(0, 0));
        game.addPoints(15);
        game.incScore();
        game.decMaxScore(3);

        session.reset();

        assertNotSame(oldSnake, session.snake());
        assertNotSame(oldCoin, session.coin());
        assertEquals(new Point(4, 4), session.snake().getHead());
        assertEquals(0, game.getScorePoints());
        assertTrue(session.obstacle().getObstacles().isEmpty());
        assertFalse(session.coin().getPos().equals(session.snake().getHead()));
        assertFalse(session.snake().getBody().contains(session.coin().getPos()));
    }
}
