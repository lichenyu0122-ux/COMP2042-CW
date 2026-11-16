package com.comp2042.model;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

/**
 * Immutable level configuration with defensive copies of its wall positions.
 */
public final class Level {
    private final String name;
    private final int columns;
    private final int rows;
    private final int target;
    private final int speedMillis;
    private final Set<Point> walls;

    /**
     * Stores a level definition and copies its wall positions.
     *
     * @param name the level name
     * @param columns the board width
     * @param rows the board height
     * @param target the required reward count
     * @param speedMillis base tick duration, or zero for difficulty defaults
     * @param walls the level wall coordinates
     */
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

    /**
     * Reads the level name.
     *
     * @return the menu name
     */
    public String name() { return name; }
    /**
     * Reads the level columns.
     *
     * @return the board width in cells
     */
    public int columns() { return columns; }
    /**
     * Reads the level rows.
     *
     * @return the board height in cells
     */
    public int rows() { return rows; }
    /**
     * Reads the level target.
     *
     * @return the required number of rewards
     */
    public int target() { return target; }
    /**
     * Reads the level speedMillis.
     *
     * @return the base movement interval in milliseconds
     */
    public int speedMillis() { return speedMillis; }
    /**
     * Reads the level walls.
     *
     * @return a deep copy of wall coordinates
     */
    public Set<Point> walls() { return copy(walls); }
}
