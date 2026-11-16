package com.comp2042.model;

import java.awt.Point;
import java.util.ArrayList;

/**
 * Tracks head and body positions and resolves wall, border, and self collisions.
 */
public class Snake {
    private boolean dead = false;
    private final Point head = new Point();
    private final ArrayList<Point> body = new ArrayList<>();
    private final int gx;
    private final int gy;
    private boolean ghostMode = false;
    private boolean noBorder;
    private Obstacle obstacle;
    private int snakeDir = 270;
    private Point previousHead;
    private ArrayList<Point> previousBody;
    private int previousDirection;

    /**
     * Places a one-segment snake at the board center facing left.
     *
     * @param x the board width in cells
     * @param y the board height in cells
     * @param noBorder whether crossing an edge wraps the snake when Ghost is inactive
     * @param obstacle the current wall state
     */
    public Snake(int x, int y, boolean noBorder, Obstacle obstacle) {
        this.obstacle = obstacle;
        this.noBorder = noBorder;
        this.head.setLocation(x / 2, y / 2);
        this.body.add(new Point(x / 2 + 1, y / 2));
        this.gx = x;
        this.gy = y;
    }

    /**
     * Advances one cell and marks lethal border, wall, or self collisions.
     *
     * @param dir U, D, L, or R movement direction
     * @throws IllegalArgumentException if dir is not U, D, L, or R
     */
    public void move(char dir) {
        previousHead = new Point(head);
        previousDirection = snakeDir;
        previousBody = new ArrayList<>();
        body.forEach(point -> previousBody.add(new Point(point)));
        body.add(new Point(head));
        switch (dir) {
            case 'U' -> { snakeDir = 0; head.translate(0, -1); }
            case 'D' -> { snakeDir = 180; head.translate(0, 1); }
            case 'L' -> { snakeDir = 270; head.translate(-1, 0); }
            case 'R' -> { snakeDir = 90; head.translate(1, 0); }
            default -> throw new IllegalArgumentException("Unknown direction: " + dir);
        }

        if (borderControl()) {
            if (ghostMode || noBorder) {
                head.setLocation(Math.floorMod(head.x, gx), Math.floorMod(head.y, gy));
            } else {
                head.setLocation(previousHead);
                dead = true;
            }
        }
        if (!dead && !ghostMode && obstacle.getObstacles().contains(head)) {
            head.setLocation(previousHead);
            dead = true;
        }
        if (!dead && hitSelf() && !body.get(0).equals(head)) {
            dead = true;
        }
    }

    /**
     * Chooses the straight-body sprite orientation at the given body index.
     *
     * @param bodyCount the body segment index
     * @return the sprite rotation in degrees
     */
    public int getBodyDir(int bodyCount) {
        if (bodyCount == 0) {
            return 0;
        } else if (body.get(bodyCount).getY() == body.get(bodyCount - 1).getY()) {
            return 270;
        } else {
            return 0;
        }
    }

    /**
     * Keeps the added tail segment for a reward, but removes it otherwise.
     *
     * @param coin the current coin
     * @return true if the head is on the supplied coin
     */
    public boolean ateCoin(Coin coin) {
        if (!head.equals(coin.getPos()) || coin instanceof PoisonCoin) {
            body.remove(0);
        }
        return head.equals(coin.getPos());
    }

    private boolean borderControl() {
        return head.getX() < 0 || head.getY() < 0 || head.getX() >= gx || head.getY() >= gy;
    }

    /**
     * Checks whether the head currently overlaps a body segment.
     *
     * @return true on a self collision
     */
    public boolean hitSelf() {
        return body.contains(head);
    }

    /**
     * Returns a new list containing the snake's existing Point objects.
     *
     * @return a new list; changing one of its points also changes the snake
     */
    public ArrayList<Point> getBody() {
        return new ArrayList<>(body);
    }

    /**
     * Provides a copy of the head position.
     *
     * @return a copy of the head coordinate
     */
    public Point getHead() {
        return new Point(head);
    }

    /**
     * Tells the game whether the last move was lethal. A shield can undo that move.
     *
     * @return true after a lethal move
     */
    public boolean getDead() {
        return dead;
    }

    /**
     * Restores the state before a lethal move absorbed by a shield.
     *
     * @throws IllegalStateException if there is no lethal move to restore
     */
    public void undoDeath() {
        if (!dead || previousHead == null) throw new IllegalStateException("No lethal move to undo");
        head.setLocation(previousHead);
        body.clear();
        previousBody.forEach(point -> body.add(new Point(point)));
        snakeDir = previousDirection;
        dead = false;
    }

    /**
     * Removes up to the requested tail segments while keeping one body segment.
     *
     * @param segments the nonnegative number of segments to remove
     * @throws IllegalArgumentException if segments is negative
     */
    public void shrink(int segments) {
        if (segments < 0) throw new IllegalArgumentException("segments must be non-negative");
        int removed = Math.max(0, Math.min(segments, body.size() - 1));
        body.subList(0, removed).clear();
    }

    /**
     * Enables or disables wall and edge crossing; self collision still applies.
     *
     * @param active whether Ghost wall and edge crossing is active
     */
    public void setGhostMode(boolean active) {
        ghostMode = active;
    }

    /**
     * The rotation used when drawing the snake head.
     *
     * @return the head direction in degrees
     */
    public int getSnakeDir() {
        return snakeDir;
    }

}
