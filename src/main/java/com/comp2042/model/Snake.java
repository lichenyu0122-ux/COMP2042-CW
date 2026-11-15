package com.comp2042.model;

import java.awt.Point;
import java.util.ArrayList;

public class Snake {
    private boolean dead = false;
    private final Point head = new Point();
    private final ArrayList<Point> body = new ArrayList<>();
    private final int gx;
    private final int gy;
    private boolean ghostMode = false;
    private boolean noBorder;
    private Obstacle obstacle;
    private int snakeDir = 270;
    private Point previousHead;
    private ArrayList<Point> previousBody;
    private int previousDirection;

    public Snake(int x, int y, boolean noBorder, Obstacle obstacle) {
        this.obstacle = obstacle;
        this.noBorder = noBorder;
        this.head.setLocation(x / 2, y / 2);
        this.body.add(new Point(x / 2 + 1, y / 2));
        this.gx = x;
        this.gy = y;
    }

    public void move(char dir) {
        previousHead = new Point(head);
        previousDirection = snakeDir;
        previousBody = new ArrayList<>();
        body.forEach(point -> previousBody.add(new Point(point)));
        body.add(new Point(head));
        switch (dir) {
            case 'U' -> { snakeDir = 0; head.translate(0, -1); }
            case 'D' -> { snakeDir = 180; head.translate(0, 1); }
            case 'L' -> { snakeDir = 270; head.translate(-1, 0); }
            case 'R' -> { snakeDir = 90; head.translate(1, 0); }
            default -> throw new IllegalArgumentException("Unknown direction: " + dir);
        }

        if (borderControl()) {
            if (ghostMode || noBorder) {
                head.setLocation(Math.floorMod(head.x, gx), Math.floorMod(head.y, gy));
            } else {
                head.setLocation(previousHead);
                dead = true;
            }
        }
        if (!dead && !ghostMode && obstacle.getObstacles().contains(head)) {
            head.setLocation(previousHead);
            dead = true;
        }
        if (!dead && hitSelf() && !body.get(0).equals(head)) {
            dead = true;
        }
    }

    public int getBodyDir(int bodyCount) {
        if (bodyCount == 0) {
            return 0;
        } else if (body.get(bodyCount).getY() == body.get(bodyCount - 1).getY()) {
            return 270;
        } else {
            return 0;
        }
    }

    public boolean ateCoin(Coin coin) {
        if (!head.equals(coin.getPos()) || coin instanceof PoisonCoin) {
            body.remove(0);
        }
        return head.equals(coin.getPos());
    }

    private boolean borderControl() {
        return head.getX() < 0 || head.getY() < 0 || head.getX() >= gx || head.getY() >= gy;
    }

    public boolean hitSelf() {
        return body.contains(head);
    }

    public ArrayList<Point> getBody() {
        return new ArrayList<>(body);
    }

    public Point getHead() {
        return new Point(head);
    }

    public boolean getDead() {
        return dead;
    }

    /** Cancels a lethal move when a shield absorbs the collision. */
    public void undoDeath() {
        if (!dead || previousHead == null) throw new IllegalStateException("No lethal move to undo");
        head.setLocation(previousHead);
        body.clear();
        previousBody.forEach(point -> body.add(new Point(point)));
        snakeDir = previousDirection;
        dead = false;
    }

    public void shrink(int segments) {
        if (segments < 0) throw new IllegalArgumentException("segments must be non-negative");
        int removed = Math.min(segments, body.size());
        body.subList(0, removed).clear();
    }

    public void setGhostMode(boolean active) {
        ghostMode = active;
    }

    public int getSnakeDir() {
        return snakeDir;
    }

}
