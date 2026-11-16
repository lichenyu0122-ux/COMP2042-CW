package com.comp2042.model;

import java.awt.Point;

/**
 * Reward coin that activates the Ghost effect when collected by a game session.
 */
public class BlueCoin extends Coin {

    /**
     * Creates a Ghost reward on a free cell.
     *
     * @param x the board width in cells
     * @param y the board height in cells
     * @param snek the current snake
     * @param obstacle the current wall state
     * @throws IllegalStateException if no free cell is available
     */
    public BlueCoin(int x, int y, Snake snek, Obstacle obstacle) {
        super(x, y, snek, obstacle);
    }

    /**
     * Creates a Ghost reward at an explicit position for internal use.
     *
     * @param position the coordinate to copy
     */
    BlueCoin(Point position) {
        super(position);
    }

}
