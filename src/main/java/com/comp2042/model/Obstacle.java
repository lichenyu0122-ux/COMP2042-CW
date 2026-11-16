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
     * Adds random walls only on free cells, up to the board wall limit.
     *
     * @param snek the current snake
     * @param coin the current coin
     * @param amount the nonnegative point penalty or requested obstacle count
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
     * Exposes the mutable wall list used by collision and rendering.
     *
     * @return the live list of wall coordinates
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
     * @param level the selected level, or null for a game without a preset level
     */
    public void load(Level level) {
        reset();
        level.walls().forEach(point -> obstacles.add(new Point(point)));
    }
}
