package com.comp2042.model;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Parses the documented header and dot/hash grid format. */
public final class LevelParser {
    public Level parse(String source) {
        String[] lines = source.strip().split("\\R");
        Map<String, String> headers = new HashMap<>();
        int separator = -1;
        for (int line = 0; line < lines.length; line++) {
            String value = lines[line].trim();
            if (value.equals("---")) {
                separator = line;
                break;
            }
            String[] pair = value.split(":", 2);
            if (pair.length != 2 || headers.putIfAbsent(pair[0].trim(), pair[1].trim()) != null) {
                throw new IllegalArgumentException("Invalid header on line " + (line + 1));
            }
        }
        if (separator < 0) throw new IllegalArgumentException("Missing --- grid separator");
        String name = required(headers, "name");
        int columns = positive(headers, "cols");
        int rows = positive(headers, "rows");
        int target = positive(headers, "target");
        int speed = positive(headers, "speed");
        if (columns < 3 || rows < 3) throw new IllegalArgumentException("Level must be at least 3 by 3");
        if (lines.length - separator - 1 != rows) throw new IllegalArgumentException("Grid row count differs from rows");

        Set<Point> walls = new HashSet<>();
        for (int row = 0; row < rows; row++) {
            String cells = lines[separator + 1 + row];
            if (cells.length() != columns) {
                throw new IllegalArgumentException("Grid width differs from cols on line " + (separator + row + 2));
            }
            for (int col = 0; col < columns; col++) {
                char cell = cells.charAt(col);
                if (cell == '#') walls.add(new Point(col, row));
                else if (cell != '.') throw new IllegalArgumentException("Unknown cell '" + cell + "' on line " + (separator + row + 2));
            }
        }
        Point head = new Point(columns / 2, rows / 2);
        Point body = new Point(head.x + 1, head.y);
        if (walls.contains(head) || walls.contains(body)) {
            throw new IllegalArgumentException("Initial snake position is blocked");
        }
        if (target > columns * rows - walls.size() - 2) {
            throw new IllegalArgumentException("Target exceeds available cells");
        }
        return new Level(name, columns, rows, target, speed, walls);
    }

    private static String required(Map<String, String> headers, String key) {
        String value = headers.get(key);
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Missing " + key);
        return value;
    }

    private static int positive(Map<String, String> headers, String key) {
        int number;
        try { number = Integer.parseInt(required(headers, key)); }
        catch (NumberFormatException ex) { throw new IllegalArgumentException("Invalid " + key, ex); }
        if (number <= 0) throw new IllegalArgumentException(key + " must be positive");
        return number;
    }
}
