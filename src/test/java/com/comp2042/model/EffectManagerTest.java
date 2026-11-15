package com.comp2042.model;

import org.junit.jupiter.api.Test;
import java.awt.Point;
import static org.junit.jupiter.api.Assertions.*;

class EffectManagerTest {
    @Test void effectsExpireByMovesAndSameTypeRefreshes() {
        EffectManager effects = new EffectManager();
        effects.activate(PowerUpFactory.create(EffectType.SLOW));
        assertEquals(0.5, effects.speedMultiplier());
        effects.tick();
        assertEquals(7, effects.remaining(EffectType.SLOW));
        effects.activate(PowerUpFactory.create(EffectType.SLOW));
        effects.activate(PowerUpFactory.create(EffectType.GHOST));
        assertEquals(8, effects.remaining(EffectType.SLOW));
        assertEquals(20, effects.remaining(EffectType.GHOST));
        for (int i = 0; i < 6; i++) effects.tick();
        assertEquals(14, effects.remaining(EffectType.GHOST));
        assertTrue(effects.isActive(EffectType.SLOW));
        effects.tick();
        effects.tick();
        assertEquals(1.0, effects.speedMultiplier());
        for (int i = 8; i < 20; i++) effects.tick();
        assertFalse(effects.isActive(EffectType.GHOST));
    }

    @Test void shieldCancelsWallOrBodyCollisionOnlyOnce() {
        GameSession wall = new GameSession(new Game(6, 6, true, 'N'));
        wall.obstacle().getObstacles().add(new Point(2, 3));
        wall.effects().activate(new ShieldEffect());
        assertEquals(GameSession.MoveResult.SHIELDED, wall.move('L'));
        assertEquals(new Point(3, 3), wall.snake().getHead());
        assertFalse(wall.snake().getDead());
        assertEquals(GameSession.MoveResult.DEAD, wall.move('L'));

        GameSession body = new GameSession(new Game(12, 12, true, 'N'));
        body.effects().activate(new ShieldEffect());
        body.move('U'); body.move('L'); body.move('D');
        assertEquals(GameSession.MoveResult.SHIELDED, body.move('R'));
        assertFalse(body.snake().getDead());
    }

    @Test void ghostPassesWallsAndWrapsBorderButDoesNotPreventSelfCollision() {
        GameSession wrap = new GameSession(new Game(4, 4, true, 'N'));
        wrap.effects().activate(new GhostEffect());
        wrap.move('U'); wrap.move('U');
        assertEquals(GameSession.MoveResult.MOVED, wrap.move('U'));
        assertEquals(new Point(2, 3), wrap.snake().getHead());

        GameSession wallExit = new GameSession(new Game(4, 4, true, 'N'));
        wallExit.obstacle().getObstacles().add(new Point(2, 3));
        wallExit.effects().activate(new GhostEffect());
        wallExit.move('U'); wallExit.move('U');
        assertEquals(GameSession.MoveResult.MOVED, wallExit.move('U'));
        assertEquals(new Point(2, 3), wallExit.snake().getHead());

        GameSession body = new GameSession(new Game(12, 12, true, 'N'));
        body.effects().activate(new GhostEffect());
        body.move('U'); body.move('L'); body.move('D');
        assertEquals(GameSession.MoveResult.DEAD, body.move('R'));
    }

    @Test void ghostStopsWrappingAfterTwentyMoves() {
        GameSession session = new GameSession(new Game(4, 4, true, 'N'));
        session.replaceCoin(new Coin(new Point(0, 0)));
        session.effects().activate(new GhostEffect());
        for (int i = 0; i < 20; i++) {
            assertEquals(GameSession.MoveResult.MOVED, session.move('U'));
            session.finishMove();
        }
        assertEquals(0, session.effects().remaining(EffectType.GHOST));
        assertEquals(new Point(2, 2), session.snake().getHead());
        assertEquals(GameSession.MoveResult.MOVED, session.move('U'));
        assertEquals(GameSession.MoveResult.MOVED, session.move('U'));
        assertEquals(GameSession.MoveResult.DEAD, session.move('U'));
    }

    @Test void ghostPassesStaticWallsOnlyWhileActive() {
        GameSession session = new GameSession(new Game(20, 20, true, 'N'));
        session.replaceCoin(new Coin(new Point(0, 0)));
        for (int x = 3; x <= 9; x++) session.obstacle().getObstacles().add(new Point(x, 10));
        session.effects().activate(new GhostEffect());
        for (int i = 0; i < 20; i++) {
            assertEquals(GameSession.MoveResult.MOVED, session.move('L'));
            session.finishMove();
        }
        assertEquals(new Point(10, 10), session.snake().getHead());
        assertEquals(0, session.effects().remaining(EffectType.GHOST));
        assertEquals(GameSession.MoveResult.DEAD, session.move('L'));
    }

    @Test void bluePickupKeepsGhostActiveLongEnoughToReachAndPassAWall() {
        GameSession session = new GameSession(new Game(20, 20, true, 'N'));
        session.replaceCoin(new BlueCoin(new Point(9, 10)));
        session.obstacle().getObstacles().add(new Point(0, 10));

        assertEquals(GameSession.MoveResult.MOVED, session.move('L'));
        assertEquals(GameSession.Pickup.REWARD, session.finishMove());
        assertEquals(20, session.effects().remaining(EffectType.GHOST));

        for (int i = 0; i < 9; i++) {
            assertEquals(GameSession.MoveResult.MOVED, session.move('L'));
        }
        assertEquals(new Point(0, 10), session.snake().getHead());
        assertFalse(session.snake().getDead());
        assertEquals(11, session.effects().remaining(EffectType.GHOST));
    }

    @Test void resetRemovesEffects() {
        GameSession session = new GameSession(new Game(8, 8, true, 'N'));
        session.effects().activate(new ShieldEffect());
        session.reset();
        assertEquals(0, session.effects().remaining(EffectType.SHIELD));
    }

    @Test void pickupsActivateEffectsThroughSession() {
        GameSession session = new GameSession(new Game(8, 8, true, 'N'));
        Snake snake = session.snake();
        Obstacle obstacle = session.obstacle();
        session.replaceCoin(new ShieldCoin(8, 8, snake, obstacle));
        session.collectCoin();
        session.replaceCoin(new SlowCoin(8, 8, snake, obstacle));
        session.collectCoin();
        session.replaceCoin(new BlueCoin(8, 8, snake, obstacle));
        session.collectCoin();
        assertEquals(20, session.effects().remaining(EffectType.SHIELD));
        assertEquals(8, session.effects().remaining(EffectType.SLOW));
        assertEquals(20, session.effects().remaining(EffectType.GHOST));
        assertEquals(3, session.game().getScore());
    }
}
