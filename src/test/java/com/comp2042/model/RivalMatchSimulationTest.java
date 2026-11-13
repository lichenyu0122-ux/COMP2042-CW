package com.comp2042.model;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RivalMatchSimulationTest {
    @Test
    void bothStagesCanRunToAScoreRaceWinner() {
        for (RivalLevel level : RivalLevel.values()) {
            RivalMatch match = new RivalMatch(level, new Random(42), null);
            RivalAi ai = new RivalAi();
            int moves = 0;
            while (match.winner() == null && moves < 2_000) {
                match.move(RivalMatch.Side.PLAYER, Heading.RIGHT);
                match.move(RivalMatch.Side.ENEMY, ai.choose(match));
                moves++;
            }

            assertNotNull(match.winner(), level + " should reach a winner");
            assertTrue(match.snake(match.winner()).score() >= RivalLevel.TARGET_SCORE);
        }
    }
}
