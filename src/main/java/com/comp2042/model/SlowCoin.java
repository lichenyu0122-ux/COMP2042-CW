package com.comp2042.model;

/**
 * Reward coin that temporarily reduces the movement rate when collected.
 */
public final class SlowCoin extends Coin {
    /**
     * Creates a Slow reward on a free cell.
     *
     * @param x the board width in cells
     * @param y the board height in cells
     * @param snake the current snake
     * @param obstacle the current wall state
     * @throws IllegalStateException if no free cell is available
     */
    public SlowCoin(int x, int y, Snake snake, Obstacle obstacle) { super(x, y, snake, obstacle); }
}
