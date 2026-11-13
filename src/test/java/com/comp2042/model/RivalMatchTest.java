package com.comp2042.model;

import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RivalMatchTest {
    @Test
    void levelsHaveDifferentBalancedLayoutsAndRates() {
        assertEquals(4, RivalLevel.MEADOW.initialObstacles().size());
        assertEquals(8, RivalLevel.CROSSROADS.initialObstacles().size());
        assertTrue(RivalLevel.MEADOW.movesPerSecond() < RivalLevel.CROSSROADS.movesPerSecond());
        assertTrue(RivalLevel.CROSSROADS.movesPerSecond() < 8.5);
    }

    @Test
    void firstSnakeToReachOneHundredWins() {
        RivalMatch match = matchWithFood(CoinSpawnPolicy.CoinType.GOLD, 15);
        match.snake(RivalMatch.Side.PLAYER).addScore(90);

        match.move(RivalMatch.Side.PLAYER, Heading.RIGHT);

        assertEquals(105, match.snake(RivalMatch.Side.PLAYER).score());
        assertEquals(RivalMatch.Side.PLAYER, match.winner());
        Point winningHead = match.snake(RivalMatch.Side.PLAYER).head();
        match.move(RivalMatch.Side.ENEMY, Heading.LEFT);
        assertEquals(winningHead, match.snake(RivalMatch.Side.PLAYER).head());
        assertEquals(new Point(15, 10), match.snake(RivalMatch.Side.ENEMY).head());
    }

    @Test
    void hittingOpponentsBodyRespawnsAndDropsExactScore() {
        RivalMatch match = matchWithFoodAtCorner();
        match.snake(RivalMatch.Side.ENEMY).addScore(13);
        RivalSnake oldEnemy = match.snake(RivalMatch.Side.ENEMY);
        match.move(RivalMatch.Side.PLAYER, Heading.DOWN);

        for (int step = 0; step < 11; step++) {
            match.move(RivalMatch.Side.ENEMY, Heading.LEFT);
        }

        assertNotSame(oldEnemy, match.snake(RivalMatch.Side.ENEMY));
        assertEquals(0, match.snake(RivalMatch.Side.ENEMY).score());
        assertEquals(13, match.foods().stream().filter(RivalFood::dropped)
                .mapToInt(RivalFood::points).sum());
        assertEquals(0, match.snake(RivalMatch.Side.PLAYER).score());
        assertTrue(match.foods().stream().filter(RivalFood::dropped)
                .allMatch(food -> Math.abs(food.position().x - 4)
                        + Math.abs(food.position().y - 10) <= 2));
    }

    @Test
    void playerAlsoDiesWhenHittingEnemyBody() {
        RivalMatch match = matchWithFoodAtCorner();
        match.snake(RivalMatch.Side.PLAYER).addScore(17);
        RivalSnake oldPlayer = match.snake(RivalMatch.Side.PLAYER);
        match.move(RivalMatch.Side.ENEMY, Heading.UP);

        for (int step = 0; step < 11; step++) {
            match.move(RivalMatch.Side.PLAYER, Heading.RIGHT);
        }

        assertNotSame(oldPlayer, match.snake(RivalMatch.Side.PLAYER));
        assertEquals(0, match.snake(RivalMatch.Side.PLAYER).score());
        assertEquals(17, match.foods().stream().filter(RivalFood::dropped)
                .mapToInt(RivalFood::points).sum());
    }

    @Test
    void headCollisionRespawnsBothAndPreservesBothScoresAsDrops() {
        RivalMatch match = matchWithFoodAtCorner();
        match.snake(RivalMatch.Side.PLAYER).addScore(9);
        match.snake(RivalMatch.Side.ENEMY).addScore(12);

        for (int step = 0; step < 11; step++) {
            match.move(RivalMatch.Side.ENEMY, Heading.LEFT);
        }

        assertEquals(0, match.snake(RivalMatch.Side.PLAYER).score());
        assertEquals(0, match.snake(RivalMatch.Side.ENEMY).score());
        assertEquals(21, match.foods().stream().filter(RivalFood::dropped)
                .mapToInt(RivalFood::points).sum());
        assertEquals(new Point(4, 10), match.snake(RivalMatch.Side.PLAYER).head());
        assertEquals(new Point(15, 10), match.snake(RivalMatch.Side.ENEMY).head());
    }

    @Test
    void bothSnakesCanUseRainbowBlueAndSilverFood() {
        RivalMatch rainbow = matchWithFood(CoinSpawnPolicy.CoinType.RAINBOW, 5);
        rainbow.move(RivalMatch.Side.PLAYER, Heading.RIGHT);
        assertTrue(rainbow.snake(RivalMatch.Side.PLAYER).speedBoosted());

        RivalMatch blue = matchWithFood(CoinSpawnPolicy.CoinType.BLUE, 5);
        blue.move(RivalMatch.Side.PLAYER, Heading.RIGHT);
        assertTrue(blue.snake(RivalMatch.Side.PLAYER).canWrap());

        RivalMatch silver = matchWithFood(CoinSpawnPolicy.CoinType.SILVER, 5);
        int before = silver.obstacles().size();
        silver.move(RivalMatch.Side.PLAYER, Heading.RIGHT);
        assertEquals(before + RivalLevel.MEADOW.silverObstacles(), silver.obstacles().size());
        assertFalse(silver.snake(RivalMatch.Side.ENEMY).speedBoosted());

        RivalMatch enemyRainbow = new RivalMatch(RivalLevel.MEADOW, new Random(4),
                new RivalFood(new Point(14, 10), CoinSpawnPolicy.CoinType.RAINBOW, 5, false));
        enemyRainbow.move(RivalMatch.Side.ENEMY, Heading.LEFT);
        assertTrue(enemyRainbow.snake(RivalMatch.Side.ENEMY).speedBoosted());

        RivalMatch enemyBlue = new RivalMatch(RivalLevel.MEADOW, new Random(4),
                new RivalFood(new Point(14, 10), CoinSpawnPolicy.CoinType.BLUE, 5, false));
        enemyBlue.move(RivalMatch.Side.ENEMY, Heading.LEFT);
        assertTrue(enemyBlue.snake(RivalMatch.Side.ENEMY).canWrap());
    }

    private static RivalMatch matchWithFood(CoinSpawnPolicy.CoinType type, int points) {
        return new RivalMatch(RivalLevel.MEADOW, new Random(4),
                new RivalFood(new Point(5, 10), type, points, false));
    }

    private static RivalMatch matchWithFoodAtCorner() {
        return new RivalMatch(RivalLevel.MEADOW, new Random(4),
                new RivalFood(new Point(0, 0), CoinSpawnPolicy.CoinType.REGULAR, 5, false));
    }
}
