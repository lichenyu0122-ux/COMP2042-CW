package com.comp2042.model;

import org.junit.jupiter.api.Test;
import java.awt.Point;
import java.util.Map;
import java.util.Set;
import java.util.ArrayDeque;
import java.util.HashSet;
import static org.junit.jupiter.api.Assertions.*;

class LevelParserTest {
    private static final String VALID = "name: Test\ntarget: 2\nspeed: 180\ncols: 4\nrows: 3\n---\n....\n....\n#...";
    private final LevelParser parser = new LevelParser();

    @Test void parsesWallsAndDefensivelyCopiesThem() {
        Level level = parser.parse(VALID);
        assertEquals(new Point(0, 2), level.walls().iterator().next());
        level.walls().clear();
        assertEquals(1, level.walls().size());
    }

    @Test void rejectsWrongRowCount() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(VALID.replace("rows: 3", "rows: 4")));
    }

    @Test void reportsUnknownCellsWithTheirLineNumber() {
        IllegalArgumentException unknown = assertThrows(IllegalArgumentException.class,
                () -> parser.parse(VALID.replace("....\n#...", "..?.\n#...")));
        assertTrue(unknown.getMessage().contains("line 8"));
    }

    @Test void rejectsBlockedStartingPosition() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(VALID.replace("....\n#...", "..#.\n#...")));
    }

    @Test void rejectsTargetAboveFreeCellCount() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(VALID.replace("target: 2", "target: 11")));
    }

    @Test void rejectsNonPositiveSpeed() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(VALID.replace("speed: 180", "speed: -1")));
    }

    @Test void catalogLoadsThreePlayableLevels() {
        Map<String, Level> levels = LevelCatalog.load();
        assertEquals(3, levels.size());
        assertEquals(Set.of("Grassland", "Maze", "Hourglass"), levels.keySet());
        assertEquals(200, levels.get("Grassland").speedMillis());
        assertEquals(180, levels.get("Maze").speedMillis());
        assertEquals(150, levels.get("Hourglass").speedMillis());
        for (Level level : levels.values()) {
            Game game = new Game(level.columns(), level.rows(), true, 'N', level.target(), level.speedMillis());
            GameSession session = new GameSession(game, level);
            assertEquals(level.walls(), new java.util.HashSet<>(session.obstacle().getObstacles()));
            assertFalse(session.coin().getPos().equals(session.snake().getHead()));
            for (int n = 0; n < level.target(); n++) game.incScore();
            assertTrue(game.gameWon());
            session.reset();
            assertEquals(level.target(), game.getTargetScore());
            assertFalse(game.gameWon());
            assertEveryFreeCellReachable(level);
        }
    }

    private static void assertEveryFreeCellReachable(Level level) {
        Set<Point> walls = level.walls();
        Set<Point> reached = new HashSet<>();
        ArrayDeque<Point> queue = new ArrayDeque<>();
        Point start = new Point(level.columns() / 2, level.rows() / 2);
        reached.add(start);
        queue.add(start);
        while (!queue.isEmpty()) {
            Point point = queue.remove();
            for (int[] step : new int[][] {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
                Point next = new Point(point.x + step[0], point.y + step[1]);
                if (next.x >= 0 && next.x < level.columns()
                        && next.y >= 0 && next.y < level.rows()
                        && !walls.contains(next) && reached.add(next)) queue.add(next);
            }
        }
        assertEquals(level.columns() * level.rows() - walls.size(), reached.size(), level.name());
    }
}
