package com.comp2042.model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collections;

/**
 * Loads the ordered level index and map resources from the classpath.
 */
public final class LevelCatalog {
    private LevelCatalog() {}

    /**
     * Reads registered map resources in index order and rejects duplicate or invalid levels.
     *
     * @return an unmodifiable map keyed by level name
     * @throws IllegalArgumentException if the index or a map is invalid
     * @throws IllegalStateException if a required resource cannot be read
     */
    public static Map<String, Level> load() {
        try {
            String index = read("levels/index.txt");
            Map<String, Level> levels = new LinkedHashMap<>();
            LevelParser parser = new LevelParser();
            for (String line : index.lines().toList()) {
                String file = line.trim();
                if (file.isEmpty() || file.startsWith("#")) continue;
                if (!file.matches("[a-zA-Z0-9_-]+\\.level")) {
                    throw new IllegalArgumentException("Invalid level filename: " + file);
                }
                Level level = parser.parse(read("levels/" + file));
                if (levels.putIfAbsent(level.name(), level) != null) {
                    throw new IllegalArgumentException("Duplicate level: " + level.name());
                }
            }
            if (levels.isEmpty()) throw new IllegalArgumentException("No levels configured");
            return Collections.unmodifiableMap(levels);
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot load levels", ex);
        }
    }

    private static String read(String path) throws IOException {
        try (InputStream stream = LevelCatalog.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) throw new IOException("Missing resource: " + path);
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
