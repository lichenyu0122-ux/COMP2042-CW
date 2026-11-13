package com.comp2042.model;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Chooses a safe route toward the most rewarding reachable bean. */
public final class RivalAi {
    public Heading choose(RivalMatch match) {
        RivalSnake enemy = match.snake(RivalMatch.Side.ENEMY);
        RivalSnake player = match.snake(RivalMatch.Side.PLAYER);
        Set<Point> blocked = new HashSet<>(match.obstacles());
        blocked.addAll(enemy.body());
        blocked.addAll(player.segments());

        Heading best = enemy.heading();
        int bestCost = Integer.MAX_VALUE;
        for (Heading candidate : Heading.values()) {
            if (enemy.heading().isOpposite(candidate)) {
                continue;
            }
            Point next = next(enemy.head(), candidate, enemy.canWrap());
            if (next == null || blocked.contains(next)) {
                continue;
            }
            int cost = routeCost(next, blocked, match.foods(), enemy.canWrap());
            if (cost < bestCost || (cost == bestCost && candidate == enemy.heading())) {
                bestCost = cost;
                best = candidate;
            }
        }
        return best;
    }

    private int routeCost(Point start, Set<Point> blocked, List<RivalFood> foods, boolean wrap) {
        ArrayDeque<Point> queue = new ArrayDeque<>();
        Set<Point> visited = new HashSet<>(blocked);
        queue.add(start);
        visited.add(start);
        int distance = 0;
        int best = Integer.MAX_VALUE;
        while (!queue.isEmpty()) {
            int layerSize = queue.size();
            for (int index = 0; index < layerSize; index++) {
                Point current = queue.removeFirst();
                for (RivalFood food : foods) {
                    if (food.position().equals(current)) {
                        best = Math.min(best, distance * 5 - Math.min(food.points(), 15));
                    }
                }
                for (Heading direction : Heading.values()) {
                    Point next = next(current, direction, wrap);
                    if (next != null && visited.add(next)) {
                        queue.addLast(next);
                    }
                }
            }
            distance++;
        }
        return best;
    }

    private Point next(Point from, Heading direction, boolean wrap) {
        Point result = new Point(from.x + direction.dx, from.y + direction.dy);
        if (wrap) {
            result.x = Math.floorMod(result.x, RivalLevel.WIDTH);
            result.y = Math.floorMod(result.y, RivalLevel.HEIGHT);
            return result;
        }
        if (result.x < 0 || result.x >= RivalLevel.WIDTH
                || result.y < 0 || result.y >= RivalLevel.HEIGHT) {
            return null;
        }
        return result;
    }
}
