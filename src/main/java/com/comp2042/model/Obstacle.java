package com.comp2042.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;

public class Obstacle {
    private ArrayList<Point> obstacles = new ArrayList<>();
    private int x;
    private int y;

    public Obstacle(int x, int y) {
       this.x = x;
       this.y = y;
    }

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

    public ArrayList<Point> getObstacles() {
        return obstacles;
    }

    public void reset() {
        obstacles.clear();
    }
}
