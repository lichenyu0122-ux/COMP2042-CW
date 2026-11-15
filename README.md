# COMP2042 Snake

GitHub: https://github.com/lichenyu0122-ux/COMP2042-CW

## Run

Use JDK 25. On Windows, run `.\mvnw.cmd javafx:run` from this folder. On macOS or Linux, run `./mvnw javafx:run`. Run `clean verify` instead of `javafx:run` to execute the tests.

## Play

Choose Easy, Normal or Hard, then choose Grassland, Maze or Hourglass. Use the arrow keys to move, `P` to pause or resume, `Tab` to restart, and `Esc` to return to the menu.

Levels are loaded from `src/main/resources/levels/`. Grassland starts without walls and needs 10 food pickups; Maze has passages and needs 15; Hourglass has narrow gates and needs 20. The level files define the map and base speed. Add another `.level` file and list it in `levels/index.txt` to add a level without changing Java code.

Blue coins marked G grant 20 moves of Ghost: the snake can pass through static walls and wrap across the board edge, but hitting its own body still ends the game. Cyan coins marked S give one shield against a lethal collision for up to 20 moves. When the shield absorbs a hit, movement pauses until you choose a valid new direction. Orange coins halve movement speed for eight moves. Picking up the same effect refreshes its duration, while different effects can run together. Purple poison marked ! is a separate hazard beside the normal food; it removes up to two body segments and deducts 10 pts, stopping at zero. Target progress does not change. The HUD shows the level, target and remaining effect durations.

## Verify

Run `.\mvnw.cmd clean verify` on Windows or `./mvnw clean verify` on macOS or Linux. The JUnit tests cover level parsing, map access, collisions, effect expiry, poison and scoring.
