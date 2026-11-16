package com.comp2042.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Stores fixed and generated wall cells for a round.
 */
public class Obstacle {
    private ArrayList<Point> obstacles = new ArrayList<>();
    private int x;
    private int y;

    /**
     * Creates empty wall state for the given board dimensions.
     *
     * @param x the board width in cells
     * @param y the board height in cells
     */
    public Obstacle(int x, int y) {
       this.x = x;
       this.y = y;
    }

    /**
     * Adds walls to free cells. The total stops at 50 or when no free cells remain.
     *
     * @param snek the current snake
     * @param coin the current coin
     * @param amount how many walls to try to add; zero or less adds none
     */
    public void newObstacles(Snake snek, Coin coin, int amount) {
        if (amount <= 0 || obstacles.size() >= 50) {
            return;
        }

        Point coinPosition = coin.getPos();
        Point snakeHead = snek.getHead();
        ArrayList<Point> snakeBody = snek.getBody();
        ArrayList<Point> freeCells = new ArrayList<>();
        for (int column = 0; column < x; column++) {
            for (int row = 0; row < y; row++) {
                Point candidate = new Point(column, row);
                if (!candidate.equals(coinPosition)
                        && !candidate.equals(snakeHead)
                        && !snakeBody.contains(candidate)
                        && !obstacles.contains(candidate)) {
                    freeCells.add(candidate);
                }
            }
        }

        Collections.shuffle(freeCells);
        int count = Math.min(amount, Math.min(50 - obstacles.size(), freeCells.size()));
        obstacles.addAll(freeCells.subList(0, count));
    }

    /**
     * Returns the wall list used for collision checks and drawing. Callers can edit it.
     *
     * @return the current, mutable list of wall coordinates
     */
    public ArrayList<Point> getObstacles() {
        return obstacles;
    }

    /**
     * Clears all current wall cells.
     */
    public void reset() {
        obstacles.clear();
    }

    /**
     * Replaces current walls with copies of the level walls.
     *
     * @param level the level whose walls should be loaded
     */
    public void load(Level level) {
        reset();
        level.walls().forEach(point -> obstacles.add(new Point(point)));
    }
}
