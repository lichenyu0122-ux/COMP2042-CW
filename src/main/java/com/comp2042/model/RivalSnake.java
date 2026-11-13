package com.comp2042.model;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** A snake in the score race, with independent score and temporary powers. */
public final class RivalSnake {
    private final int width;
    private final int height;
    private final ArrayDeque<Point> segments = new ArrayDeque<>();
    private Heading heading;
    private int score;
    private int speedMoves;
    private int wrapMoves;

    RivalSnake(int width, int height, Point start, Heading heading) {
        this.width = width;
        this.height = height;
        this.heading = heading;
        segments.addFirst(new Point(start));
        segments.addLast(new Point(start.x - heading.dx, start.y - heading.dy));
    }

    public Point head() {
        return new Point(segments.getFirst());
    }

    public List<Point> segments() {
        List<Point> copy = new ArrayList<>();
        for (Point point : segments) {
            copy.add(new Point(point));
        }
        return copy;
    }

    public List<Point> body() {
        return segments().subList(1, segments.size());
    }

    public Heading heading() {
        return heading;
    }

    public int score() {
        return score;
    }

    public boolean speedBoosted() {
        return speedMoves > 0;
    }

    public boolean canWrap() {
        return wrapMoves > 0;
    }

    public boolean turnTo(Heading next) {
        if (next == null || heading.isOpposite(next)) {
            return false;
        }
        heading = next;
        return true;
    }

    public Point preview() {
        Point next = head();
        next.translate(heading.dx, heading.dy);
        if (canWrap()) {
            next.x = Math.floorMod(next.x, width);
            next.y = Math.floorMod(next.y, height);
        }
        return next;
    }

    boolean move(boolean grow, Set<Point> obstacles) {
        Point next = preview();
        if (next.x < 0 || next.x >= width || next.y < 0 || next.y >= height
                || obstacles.contains(next)
                || (segments.contains(next) && (grow || !next.equals(segments.getLast())))) {
            return false;
        }
        segments.addFirst(next);
        if (!grow) {
            segments.removeLast();
        }
        if (speedMoves > 0) {
            speedMoves--;
        }
        if (wrapMoves > 0) {
            wrapMoves--;
        }
        return true;
    }

    void addScore(int points) {
        score += points;
    }

    void giveSpeedBoost() {
        speedMoves = 12;
    }

    void giveWrap() {
        wrapMoves = 16;
    }
}
