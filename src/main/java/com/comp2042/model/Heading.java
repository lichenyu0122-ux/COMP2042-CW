package com.comp2042.model;

/** Grid movement direction without a JavaFX dependency. */
public enum Heading {
    UP(0, -1), RIGHT(1, 0), DOWN(0, 1), LEFT(-1, 0);

    public final int dx;
    public final int dy;

    Heading(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public boolean isOpposite(Heading other) {
        return dx == -other.dx && dy == -other.dy;
    }
}
