package com.comp2042.model;

/**
 * Reward coin whose speed bonus applies until the next reward is collected.
 */
public class RainbowCoin extends Coin {

    /**
     * Creates a speed-bonus reward on a free cell.
     *
     * @param x the board width in cells
     * @param y the board height in cells
     * @param snek the current snake
     * @param obstacle the current wall state
     * @throws IllegalStateException if no free cell is available
     */
    public RainbowCoin(int x, int y, Snake snek, Obstacle obstacle) {
        super(x, y, snek, obstacle);
    }

    /**
     * Provides the point value of a rainbow reward.
     *
     * @return the rainbow coin point value
     */
    public int getPoints() {
        int points = 5;
        return points;
    }

    /**
     * Provides the movement-rate bonus applied by the controller.
     *
     * @return the extra moves per second
     */
    public double getSpeedBonus() {
        double speedBonus = 5;
        return speedBonus;
    }

}
