package com.comp2042.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

/**
 * Places a reward on an unoccupied board cell and supplies its point value.
 */
public class Coin {
    private final Point pos;

    /**
     * Copies an explicit position for a coin subclass.
     *
     * @param position the position to copy
     */
    protected Coin(Point position) { pos = new Point(position); }

    /**
     * Places a coin randomly on a free board cell.
     *
     * @param x the board width in cells
     * @param y the board height in cells
     * @param snek the current snake
     * @param obstacle the current wall state
     * @throws IllegalStateException if no free cell is available
     */
    public Coin(int x, int y, Snake snek, Obstacle obstacle) {
        this(x, y, snek, obstacle, null);
    }

    /**
     * Places a coin on a free cell, excluding one additional position.
     *
     * @param x the board width in cells
     * @param y the board height in cells
     * @param snek the current snake
     * @param obstacle the current wall state
     * @param excluded an additional occupied cell to avoid
     * @throws IllegalStateException if no free cell is available
     */
    protected Coin(int x, int y, Snake snek, Obstacle obstacle, Point excluded) {
        ArrayList<Point> free = new ArrayList<>();
        Point head = snek.getHead();
        ArrayList<Point> body = snek.getBody();
        ArrayList<Point> walls = obstacle.getObstacles();
        for (int column = 0; column < x; column++) {
            for (int row = 0; row < y; row++) {
                Point candidate = new Point(column, row);
                if (!candidate.equals(head) && !body.contains(candidate)
                        && !walls.contains(candidate) && !candidate.equals(excluded)) {
                    free.add(candidate);
                }
            }
        }
        if (free.isEmpty()) throw new IllegalStateException("No free cell for coin");
        pos = free.get(new Random().nextInt(free.size()));
    }

    /**
     * Provides a copy of the coin position.
     *
     * @return a copy of the board coordinate
     */
    public Point getPos() {
        return new Point(pos);
    }

    /**
     * Provides the value credited when this reward is collected.
     *
     * @return the reward point value
     */
    public int getPoints() {
        return 5;
    }

}
