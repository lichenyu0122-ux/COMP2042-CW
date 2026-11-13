package com.comp2042.model;

import org.junit.jupiter.api.Test;
import java.awt.Point;
import java.util.Map;
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

    @Test void rejectsInvalidMaps() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(VALID.replace("rows: 3", "rows: 4")));
        assertThrows(IllegalArgumentException.class, () -> parser.parse(VALID.replace("....\n#...", "..?\n#...")));
        assertThrows(IllegalArgumentException.class, () -> parser.parse(VALID.replace("....\n#...", "..#.\n#...")));
        assertThrows(IllegalArgumentException.class, () -> parser.parse(VALID.replace("target: 2", "target: 11")));
        assertThrows(IllegalArgumentException.class, () -> parser.parse(VALID.replace("speed: 180", "speed: -1")));
    }

    @Test void catalogLoadsThreePlayableLevels() {
        Map<String, Level> levels = LevelCatalog.load();
        assertEquals(3, levels.size());
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
        }
    }
}
