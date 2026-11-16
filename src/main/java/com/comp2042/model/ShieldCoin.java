package com.comp2042.model;

/**
 * Reward coin that activates a one-hit shield when collected.
 */
public final class ShieldCoin extends Coin {
    /**
     * Creates a Shield reward on a free cell.
     *
     * @param x the board width in cells
     * @param y the board height in cells
     * @param snake the current snake
     * @param obstacle the current wall state
     * @throws IllegalStateException if no free cell is available
     */
    public ShieldCoin(int x, int y, Snake snake, Obstacle obstacle) { super(x, y, snake, obstacle); }
}
