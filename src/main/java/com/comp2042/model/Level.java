package com.comp2042.model;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

/** Immutable rules and wall layout loaded from a level file. */
public final class Level {
    private final String name;
    private final int columns;
    private final int rows;
    private final int target;
    private final int speedMillis;
    private final Set<Point> walls;

    public Level(String name, int columns, int rows, int target, int speedMillis, Set<Point> walls) {
        this.name = name;
        this.columns = columns;
        this.rows = rows;
        this.target = target;
        this.speedMillis = speedMillis;
        this.walls = copy(walls);
    }

    private static Set<Point> copy(Set<Point> points) {
        Set<Point> result = new HashSet<>();
        points.forEach(point -> result.add(new Point(point)));
        return result;
    }

    public String name() { return name; }
    public int columns() { return columns; }
    public int rows() { return rows; }
    public int target() { return target; }
    public int speedMillis() { return speedMillis; }
    public Set<Point> walls() { return copy(walls); }
}
