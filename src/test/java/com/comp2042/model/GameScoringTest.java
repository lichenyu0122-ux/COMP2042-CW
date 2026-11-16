package com.comp2042.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameScoringTest {

    @Test
    void addPointsAccumulatesScore() {
        Game game = new Game(20, 20, false, 'N');

        game.addPoints(10);
        game.addPoints(5);

        assertEquals(15, game.getScorePoints());
    }

    @Test
    void resetScoreClearsPoints() {
        Game game = new Game(20, 20, false, 'N');

        game.addPoints(15);
        game.resetScore();

        assertEquals(0, game.getScorePoints());
    }

    @Test
    void poisonPenaltyCannotMakePointsNegative() {
        Game game = new Game(20, 20, false, 'N');
        game.addPoints(5);
        game.deductPoints(10);
        assertEquals(0, game.getScorePoints());
    }

    @Test
    void resetClearsProgressButKeepsTheLevelTarget() {
        Game game = new Game(8, 8, false, 'N', 2, 200);
        game.incScore();
        game.addPoints(5);

        game.resetScore();

        assertEquals(0, game.getScore());
        assertEquals(0, game.getScorePoints());
        assertEquals(2, game.getTargetScore());
        game.incScore();
        assertFalse(game.gameWon());
        game.incScore();
        assertTrue(game.gameWon());
    }

    @Test
    void gameWonBecomesTrueAtMaximumScore() {
        Game game = new Game(2, 2, false, 'N');

        assertFalse(game.gameWon());

        game.incScore();
        assertFalse(game.gameWon());

        game.incScore();
        assertTrue(game.gameWon());
    }
}
