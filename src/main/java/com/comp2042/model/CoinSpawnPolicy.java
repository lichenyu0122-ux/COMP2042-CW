package com.comp2042.model;

public final class CoinSpawnPolicy {

    public enum CoinType {
        REGULAR,
        GOLD,
        RAINBOW,
        BLUE,
        SILVER,
        SHIELD,
        SLOW,
        POISON
    }

    private CoinSpawnPolicy() {
    }

    public static CoinType select(boolean powerUp, int roll, boolean silverBlocked) {
        if (!powerUp) {
            return CoinType.REGULAR;
        }

        return switch (roll) {
            case 1 -> CoinType.GOLD;
            case 2 -> CoinType.RAINBOW;
            case 3 -> CoinType.BLUE;
            case 4 -> silverBlocked ? CoinType.REGULAR : CoinType.SILVER;
            case 5 -> CoinType.SHIELD;
            case 6 -> CoinType.SLOW;
            case 7 -> CoinType.POISON;
            default -> CoinType.REGULAR;
        };
    }
}
