package com.comp2042.model;

import java.awt.Point;
import java.util.LinkedHashSet;
import java.util.Set;

/** The two slower rival stages and their initial obstacle layouts. */
public enum RivalLevel {
    MEADOW("Rival 1: Meadow", 4.5, 1, new int[][] {
            {8, 5}, {11, 5}, {8, 14}, {11, 14}
    }),
    CROSSROADS("Rival 2: Crossroads", 5.5, 2, new int[][] {
            {8, 5}, {11, 5}, {8, 14}, {11, 14},
            {7, 8}, {12, 8}, {7, 12}, {12, 12}
    });

    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;
    public static final int TARGET_SCORE = 100;

    private final String title;
    private final double movesPerSecond;
    private final int silverObstacles;
    private final int[][] positions;

    RivalLevel(String title, double movesPerSecond, int silverObstacles, int[][] positions) {
        this.title = title;
        this.movesPerSecond = movesPerSecond;
        this.silverObstacles = silverObstacles;
        this.positions = positions;
    }

    public String title() {
        return title;
    }

    public double movesPerSecond() {
        return movesPerSecond;
    }

    public int silverObstacles() {
        return silverObstacles;
    }

    public Set<Point> initialObstacles() {
        Set<Point> result = new LinkedHashSet<>();
        for (int[] position : positions) {
            result.add(new Point(position[0], position[1]));
        }
        return result;
    }
}
