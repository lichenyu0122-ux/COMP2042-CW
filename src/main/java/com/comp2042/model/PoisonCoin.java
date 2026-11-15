package com.comp2042.model;

import java.awt.Point;

/** An unwanted pickup that removes two body segments and awards no points. */
public final class PoisonCoin extends Coin {
    public PoisonCoin(int x, int y, Snake snake, Obstacle obstacle) {
        super(x, y, snake, obstacle);
    }

    public PoisonCoin(int x, int y, Snake snake, Obstacle obstacle, Coin reward) {
        super(x, y, snake, obstacle, reward.getPos());
    }

    PoisonCoin(Point position) { super(position); }

    @Override public int getPoints() { return 0; }
}
