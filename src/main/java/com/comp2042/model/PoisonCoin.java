package com.comp2042.model;

import java.awt.Point;

/**
 * Separate hazardous pickup that reduces body length and points without advancing the target.
 */
public final class PoisonCoin extends Coin {
    /**
     * Creates poison on a free cell.
     *
     * @param x the board width in cells
     * @param y the board height in cells
     * @param snake the current snake
     * @param obstacle the current wall state
     * @throws IllegalStateException if no free cell is available
     */
    public PoisonCoin(int x, int y, Snake snake, Obstacle obstacle) {
        super(x, y, snake, obstacle);
    }

    /**
     * Creates poison on a free cell distinct from the current reward.
     *
     * @param x the board width in cells
     * @param y the board height in cells
     * @param snake the current snake
     * @param obstacle the current wall state
     * @param reward the reward cell to exclude
     * @throws IllegalStateException if no free cell is available
     */
    public PoisonCoin(int x, int y, Snake snake, Obstacle obstacle, Coin reward) {
        super(x, y, snake, obstacle, reward.getPos());
    }

    /**
     * Copies an explicit position for internal placement.
     *
     * @param position the position to copy
     */
    PoisonCoin(Point position) { super(position); }

    /**
     * Provides zero points because poison is a penalty.
     *
     * @return zero
     */
    @Override public int getPoints() { return 0; }
}
