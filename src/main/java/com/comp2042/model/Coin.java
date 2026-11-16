package com.comp2042.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class Coin {
    private final Point pos;

    protected Coin(Point position) { pos = new Point(position); }

    public Coin(int x, int y, Snake snek, Obstacle obstacle) {
        this(x, y, snek, obstacle, null);
    }

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

    public Point getPos() {
        return new Point(pos);
    }

    public int getPoints() {
        return 5;
    }

}
