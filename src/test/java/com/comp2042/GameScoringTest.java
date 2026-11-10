package com.comp2042;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void gameWonBecomesTrueAtMaximumScore() {
        Game game = new Game(2, 2, false, 'N');

        assertEquals(false, game.gameWon());

        game.incScore();
        assertEquals(false, game.gameWon());

        game.incScore();
        assertEquals(true, game.gameWon());
    }
}