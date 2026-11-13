package com.comp2042.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

/** Rules for two snakes racing to 100 points on a shared board. */
public final class RivalMatch {
    public enum Side { PLAYER, ENEMY }

    private final RivalLevel level;
    private final Random random;
    private final RivalFood firstFood;
    private final EnumMap<Side, RivalSnake> snakes = new EnumMap<>(Side.class);
    private final Set<Point> obstacles = new LinkedHashSet<>();
    private final List<RivalFood> foods = new ArrayList<>();
    private Side winner;

    public RivalMatch(RivalLevel level) {
        this(level, new Random(), null);
    }

    RivalMatch(RivalLevel level, Random random, RivalFood firstFood) {
        this.level = Objects.requireNonNull(level);
        this.random = Objects.requireNonNull(random);
        this.firstFood = firstFood;
        reset();
    }

    public RivalLevel level() {
        return level;
    }

    public RivalSnake snake(Side side) {
        return snakes.get(side);
    }

    public List<RivalFood> foods() {
        return List.copyOf(foods);
    }

    public Set<Point> obstacles() {
        Set<Point> copy = new LinkedHashSet<>();
        for (Point obstacle : obstacles) {
            copy.add(new Point(obstacle));
        }
        return copy;
    }

    public Side winner() {
        return winner;
    }

    public void reset() {
        winner = null;
        snakes.clear();
        foods.clear();
        obstacles.clear();
        obstacles.addAll(level.initialObstacles());
        snakes.put(Side.PLAYER, createSnake(Side.PLAYER));
        snakes.put(Side.ENEMY, createSnake(Side.ENEMY));
        if (firstFood == null) {
            spawnMainFood();
        } else {
            if (!freeCells().contains(firstFood.position())) {
                throw new IllegalArgumentException("Initial food must be on a free cell");
            }
            foods.add(firstFood);
        }
    }

    public void move(Side side, Heading requested) {
        if (winner != null) {
            return;
        }
        RivalSnake mover = snakes.get(side);
        RivalSnake other = snakes.get(other(side));
        mover.turnTo(requested);
        Point next = mover.preview();
        RivalFood eaten = foodAt(next);
        if (!mover.move(eaten != null, obstacles)) {
            kill(side);
            return;
        }

        if (mover.head().equals(other.head())) {
            killBoth();
            return;
        }
        if (other.body().contains(mover.head())) {
            kill(side);
            return;
        }

        if (eaten != null) {
            foods.remove(eaten);
            mover.addScore(eaten.points());
            applyPower(mover, eaten);
            if (mover.score() >= RivalLevel.TARGET_SCORE) {
                winner = side;
                return;
            }
            if (!eaten.dropped()) {
                spawnMainFood();
            }
        }
    }

    private void applyPower(RivalSnake mover, RivalFood eaten) {
        if (eaten.dropped()) {
            return;
        }
        switch (eaten.type()) {
            case RAINBOW -> mover.giveSpeedBoost();
            case BLUE -> mover.giveWrap();
            case SILVER -> addSilverObstacles();
            default -> { }
        }
    }

    private void addSilverObstacles() {
        int maximum = level.initialObstacles().size() + 6;
        int count = Math.min(level.silverObstacles(), maximum - obstacles.size());
        List<Point> free = freeCells();
        Collections.shuffle(free, random);
        for (int index = 0; index < Math.min(count, free.size()); index++) {
            obstacles.add(free.get(index));
        }
    }

    private void kill(Side side) {
        RivalSnake dead = snakes.get(side);
        int points = dead.score();
        Point center = dead.head();
        snakes.put(side, createSnake(side));
        spawnDrops(points, center);
    }

    private void killBoth() {
        RivalSnake player = snakes.get(Side.PLAYER);
        RivalSnake enemy = snakes.get(Side.ENEMY);
        int playerPoints = player.score();
        int enemyPoints = enemy.score();
        Point playerCenter = player.head();
        Point enemyCenter = enemy.head();
        snakes.clear();
        snakes.put(Side.PLAYER, createSnake(Side.PLAYER));
        snakes.put(Side.ENEMY, createSnake(Side.ENEMY));
        spawnDrops(playerPoints, playerCenter);
        spawnDrops(enemyPoints, enemyCenter);
    }

    private void spawnDrops(int points, Point center) {
        if (points <= 0) {
            return;
        }
        List<Point> free = freeCells();
        Collections.shuffle(free, random);
        free.sort(Comparator.comparingInt(point ->
                Math.abs(point.x - center.x) + Math.abs(point.y - center.y)));
        int remaining = points;
        for (int index = 0; index < free.size() && remaining > 0; index++) {
            int value = index == free.size() - 1 ? remaining : Math.min(5, remaining);
            foods.add(new RivalFood(free.get(index), CoinSpawnPolicy.CoinType.REGULAR, value, true));
            remaining -= value;
        }
    }

    private RivalSnake createSnake(Side side) {
        Point home = side == Side.PLAYER ? new Point(4, 10) : new Point(15, 10);
        Heading heading = side == Side.PLAYER ? Heading.RIGHT : Heading.LEFT;
        List<Point> candidates = new ArrayList<>();
        for (int x = 0; x < RivalLevel.WIDTH; x++) {
            for (int y = 0; y < RivalLevel.HEIGHT; y++) {
                candidates.add(new Point(x, y));
            }
        }
        candidates.sort(Comparator.comparingInt(point ->
                Math.abs(point.x - home.x) + Math.abs(point.y - home.y)));
        for (Point start : candidates) {
            Point tail = new Point(start.x - heading.dx, start.y - heading.dy);
            if (inBounds(tail) && isFree(start) && isFree(tail)) {
                return new RivalSnake(RivalLevel.WIDTH, RivalLevel.HEIGHT, start, heading);
            }
        }
        throw new IllegalStateException("No room to respawn a snake");
    }

    private void spawnMainFood() {
        List<Point> free = freeCells();
        if (free.isEmpty()) {
            return;
        }
        Point position = free.get(random.nextInt(free.size()));
        CoinSpawnPolicy.CoinType type = switch (random.nextInt(16)) {
            case 0 -> CoinSpawnPolicy.CoinType.GOLD;
            case 1 -> CoinSpawnPolicy.CoinType.RAINBOW;
            case 2 -> CoinSpawnPolicy.CoinType.BLUE;
            case 3 -> CoinSpawnPolicy.CoinType.SILVER;
            default -> CoinSpawnPolicy.CoinType.REGULAR;
        };
        int points = type == CoinSpawnPolicy.CoinType.GOLD ? 15 : 5;
        foods.add(new RivalFood(position, type, points, false));
    }

    private List<Point> freeCells() {
        List<Point> result = new ArrayList<>();
        for (int x = 0; x < RivalLevel.WIDTH; x++) {
            for (int y = 0; y < RivalLevel.HEIGHT; y++) {
                Point candidate = new Point(x, y);
                if (isFree(candidate)) {
                    result.add(candidate);
                }
            }
        }
        return result;
    }

    private boolean isFree(Point point) {
        if (obstacles.contains(point) || foodAt(point) != null) {
            return false;
        }
        for (RivalSnake snake : snakes.values()) {
            if (snake.segments().contains(point)) {
                return false;
            }
        }
        return true;
    }

    private RivalFood foodAt(Point point) {
        for (RivalFood food : foods) {
            if (food.position().equals(point)) {
                return food;
            }
        }
        return null;
    }

    private static boolean inBounds(Point point) {
        return point.x >= 0 && point.x < RivalLevel.WIDTH
                && point.y >= 0 && point.y < RivalLevel.HEIGHT;
    }

    private static Side other(Side side) {
        return side == Side.PLAYER ? Side.ENEMY : Side.PLAYER;
    }
}
