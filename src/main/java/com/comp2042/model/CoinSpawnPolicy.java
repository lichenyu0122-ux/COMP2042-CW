package com.comp2042.model;

/**
 * Selects the next reward or hazard type from a controller-supplied random roll.
 */
public final class CoinSpawnPolicy {

    /**
     * Pickup types that the controller can spawn after collecting a reward.
     */
    public enum CoinType {
        /** Ordinary reward. */
        REGULAR,
        /** Higher-value reward. */
        GOLD,
        /** Reward with a movement-rate bonus. */
        RAINBOW,
        /** Reward that activates Ghost. */
        BLUE,
        /** Reward that generates obstacles. */
        SILVER,
        /** Reward that activates Shield. */
        SHIELD,
        /** Reward that activates Slow. */
        SLOW,
        /** Separate hazardous pickup. */
        POISON
    }

    private CoinSpawnPolicy() {
    }

    /**
     * Chooses a pickup type; disabled power-ups and blocked silver yield a regular coin.
     *
     * @param powerUp whether special pickups are enabled
     * @param roll the controller-generated selection roll
     * @param silverBlocked whether silver is disallowed by walls or poison
     * @return the selected pickup type
     */
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
