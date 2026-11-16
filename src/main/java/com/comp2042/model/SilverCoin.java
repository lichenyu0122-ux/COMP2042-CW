package com.comp2042.model;

/**
 * Reward coin that causes the controller to add obstacles when allowed.
 */
public class SilverCoin extends Coin {

    /**
     * Creates an obstacle-generating reward on a free cell.
     *
     * @param x the board width in cells
     * @param y the board height in cells
     * @param snek the current snake
     * @param obstacle the current wall state
     * @throws IllegalStateException if no free cell is available
     */
    public SilverCoin(int x, int y, Snake snek, Obstacle obstacle) {
        super(x, y, snek, obstacle);
    }

    /**
     * Provides the point value of a silver reward.
     *
     * @return the silver coin point value
     */
    public int getPoints() {
        int points = 5;
        return points;
    }
}
