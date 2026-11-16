package com.comp2042.model;

/**
 * Reward coin with a higher point value than a regular coin.
 */
public class GoldCoin extends Coin {

    /**
     * Creates a high-value reward on a free cell.
     *
     * @param x the board width in cells
     * @param y the board height in cells
     * @param snek the current snake
     * @param obstacle the current wall state
     * @throws IllegalStateException if no free cell is available
     */
    public GoldCoin(int x, int y, Snake snek, Obstacle obstacle) {
        super(x, y, snek, obstacle);
    }

    /**
     * Provides the higher point value of a gold reward.
     *
     * @return the gold coin point value
     */
    public int getPoints() {
        int points = 15;
        return points;
    }
}
