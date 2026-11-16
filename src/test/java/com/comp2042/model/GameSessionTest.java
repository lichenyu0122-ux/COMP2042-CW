package com.comp2042.model;

import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

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

        session.reset();

        assertNotSame(oldSnake, session.snake());
        assertNotSame(oldCoin, session.coin());
        assertEquals(new Point(4, 4), session.snake().getHead());
        assertEquals(0, game.getScorePoints());
        assertEquals(0, game.getScore());
        assertEquals(62, game.getTargetScore());
        assertTrue(session.obstacle().getObstacles().isEmpty());
        assertFalse(session.coin().getPos().equals(session.snake().getHead()));
        assertFalse(session.snake().getBody().contains(session.coin().getPos()));
    }

    @Test
    void levelCompletesAtItsTargetAndCanBeRestarted() {
        Level level = new Level("Test", 8, 8, 2, 200, Set.of());
        Game game = new Game(8, 8, true, 'N', level.target(), level.speedMillis());
        GameSession session = new GameSession(game, level);

        session.replaceCoin(new Coin(new Point(3, 4)));
        assertEquals(GameSession.MoveResult.MOVED, session.move('L'));
        assertEquals(GameSession.Pickup.REWARD, session.finishMove());
        assertFalse(game.gameWon());

        session.replaceCoin(new Coin(new Point(2, 4)));
        assertEquals(GameSession.MoveResult.MOVED, session.move('L'));
        assertEquals(GameSession.Pickup.REWARD, session.finishMove());
        assertEquals(2, game.getScore());
        assertTrue(game.gameWon());

        session.reset();
        assertEquals(0, game.getScore());
        assertEquals(2, game.getTargetScore());
        assertFalse(game.gameWon());
    }

    @Test
    void resetRestoresLevelWallsAndPlacesItemsOnlyOnFreeCells() {
        Set<Point> walls = Set.of(new Point(1, 1), new Point(2, 2));
        Level level = new Level("Test", 8, 8, 2, 200, walls);
        GameSession session = new GameSession(new Game(8, 8, true, 'N', 2, 200), level);
        session.obstacle().getObstacles().clear();
        session.obstacle().getObstacles().add(new Point(0, 0));

        session.reset();

        assertEquals(walls, new HashSet<>(session.obstacle().getObstacles()));
        assertFalse(walls.contains(session.snake().getHead()));
        assertFalse(walls.contains(session.coin().getPos()));
        assertFalse(session.snake().getBody().contains(session.coin().getPos()));
    }
}
