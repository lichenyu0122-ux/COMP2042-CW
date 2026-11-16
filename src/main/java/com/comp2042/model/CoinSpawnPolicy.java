package com.comp2042.model;

/**
 * Selects the next reward or hazard type from a controller-supplied random roll.
 */
public final class CoinSpawnPolicy {

    /**
     * Pickup types that the controller can spawn after collecting a reward.
     */
    public enum CoinType {
        /** A regular coin with no extra effect. */
        REGULAR,
        /** A gold coin worth more points than a regular one. */
        GOLD,
        /** A rainbow coin that speeds up movement until the next reward. */
        RAINBOW,
        /** A blue coin that lets the snake cross walls and board edges. */
        BLUE,
        /** When silver is collected, the controller adds walls. Silver cannot spawn while poison or walls are already on the board. */
        SILVER,
        /** A shield coin that can save the snake from one lethal move. */
        SHIELD,
        /** An orange coin that temporarily slows the snake. */
        SLOW,
        /** A purple hazard spawned alongside a regular reward; GameSession applies its penalty. */
        POISON
    }

    private CoinSpawnPolicy() {
    }

    /**
     * Maps a random roll to a coin type. Power-ups off always gives REGULAR; a blocked silver roll also gives REGULAR.
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
