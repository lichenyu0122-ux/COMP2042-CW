package com.comp2042;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CoinSpawnPolicyTest {

    @Test
    void normalModeSelectsRegularCoin() {
        assertEquals(
                CoinSpawnPolicy.CoinType.REGULAR,
                CoinSpawnPolicy.select(false, 2, false)
        );
    }

    @Test
    void rollOneSelectsGoldCoin() {
        assertEquals(
                CoinSpawnPolicy.CoinType.GOLD,
                CoinSpawnPolicy.select(true, 1, false)
        );
    }

    @Test
    void rollTwoSelectsRainbowCoin() {
        assertEquals(
                CoinSpawnPolicy.CoinType.RAINBOW,
                CoinSpawnPolicy.select(true, 2, false)
        );
    }

    @Test
    void rollThreeSelectsBlueCoin() {
        assertEquals(
                CoinSpawnPolicy.CoinType.BLUE,
                CoinSpawnPolicy.select(true, 3, false)
        );
    }

    @Test
    void rollFourSelectsSilverCoinWhenThereAreNoObstacles() {
        assertEquals(
                CoinSpawnPolicy.CoinType.SILVER,
                CoinSpawnPolicy.select(true, 4, false)
        );
    }

    @Test
    void rollFourSelectsRegularCoinWhenObstaclesExist() {
        assertEquals(
                CoinSpawnPolicy.CoinType.REGULAR,
                CoinSpawnPolicy.select(true, 4, true)
        );
    }

    @Test
    void otherRollsSelectRegularCoin() {
        assertEquals(
                CoinSpawnPolicy.CoinType.REGULAR,
                CoinSpawnPolicy.select(true, 0, false)
        );
    }
}