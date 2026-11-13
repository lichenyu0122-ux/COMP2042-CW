package com.comp2042.model;

import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class RivalAiTest {
    @Test
    void choosesAReachableBean() {
        RivalMatch match = new RivalMatch(RivalLevel.MEADOW, new Random(3),
                new RivalFood(new Point(15, 9), CoinSpawnPolicy.CoinType.REGULAR, 5, false));

        assertEquals(Heading.UP, new RivalAi().choose(match));
    }

    @Test
    void avoidsThePlayersHeadAndBody() {
        RivalMatch match = new RivalMatch(RivalLevel.MEADOW, new Random(3),
                new RivalFood(new Point(0, 10), CoinSpawnPolicy.CoinType.REGULAR, 5, false));
        for (int step = 0; step < 10; step++) {
            match.move(RivalMatch.Side.ENEMY, Heading.LEFT);
        }

        assertEquals(new Point(5, 10), match.snake(RivalMatch.Side.ENEMY).head());
        assertNotEquals(Heading.LEFT, new RivalAi().choose(match));
    }
}
