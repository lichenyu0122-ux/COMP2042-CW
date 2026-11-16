package com.comp2042.model;

import java.awt.Point;

/**
 * A purple hazard placed apart from the reward coin. When the snake eats it, GameSession applies the body and point penalties; the target count stays the same.
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
     * Poison itself awards no points; GameSession handles the penalty.
     *
     * @return zero
     */
    @Override public int getPoints() { return 0; }
}
