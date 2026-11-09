package com.comp2042;

public final class CoinSpawnPolicy {

    enum CoinType {
        REGULAR,
        GOLD,
        RAINBOW,
        BLUE,
        SILVER
    }

    private CoinSpawnPolicy() {
    }

    static CoinType select(boolean powerUp, int roll, boolean hasObstacles) {
        if (!powerUp) {
            return CoinType.REGULAR;
        }

        return switch (roll) {
            case 1 -> CoinType.GOLD;
            case 2 -> CoinType.RAINBOW;
            case 3 -> CoinType.BLUE;
            case 4 -> hasObstacles ? CoinType.REGULAR : CoinType.SILVER;
            default -> CoinType.REGULAR;
        };
    }
}