package com.comp2042.model;

import java.awt.Point;
import java.util.Objects;

/** A shared bean or a bean dropped when a snake dies. */
public record RivalFood(Point position, CoinSpawnPolicy.CoinType type, int points, boolean dropped) {
    public RivalFood {
        position = new Point(Objects.requireNonNull(position));
        Objects.requireNonNull(type);
        if (points <= 0) {
            throw new IllegalArgumentException("Food points must be positive");
        }
    }

    @Override
    public Point position() {
        return new Point(position);
    }
}
