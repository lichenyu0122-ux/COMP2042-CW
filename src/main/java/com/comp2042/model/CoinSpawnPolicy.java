package com.comp2042.model;

public final class CoinSpawnPolicy {

    public enum CoinType {
        REGULAR,
        GOLD,
        RAINBOW,
        BLUE,
        SILVER,
        SHIELD,
        SLOW
    }

    private CoinSpawnPolicy() {
    }

    public static CoinType select(boolean powerUp, int roll, boolean hasObstacles) {
        if (!powerUp) {
            return CoinType.REGULAR;
        }

        return switch (roll) {
            case 1 -> CoinType.GOLD;
            case 2 -> CoinType.RAINBOW;
            case 3 -> CoinType.BLUE;
            case 4 -> hasObstacles ? CoinType.REGULAR : CoinType.SILVER;
            case 5 -> CoinType.SHIELD;
            case 6 -> CoinType.SLOW;
            default -> CoinType.REGULAR;
        };
    }
}
