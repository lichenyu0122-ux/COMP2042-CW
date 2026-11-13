# COMP2042 Snake

GitHub repository: https://github.com/lichenyu0122-ux/COMP2042-CW

## Run

Use JDK 25 and run `./mvnw javafx:run` from the repository root. Run `./mvnw clean verify` for the test suite.

## Play

Choose a difficulty and a level in the main menu. Use the arrow keys to steer, `P` to pause, `Tab` to restart, `Esc` to return to the menu, and `+`/`-` to toggle full screen.

The three levels are loaded from `src/main/resources/levels/`: Grassland has no fixed walls and a target of 10 food pickups; Maze has static passages and a target of 15; Hourglass has a narrow centre and a target of 20. Level files set the map, target and tick speed. To add a level, create a `.level` file and add its filename to `levels/index.txt`; the menu reads that index without a Java code change. `#` means wall and `.` means free space. `LevelParser` validates dimensions, characters, the start position and the target.

## Items and penalties

- Red, gold, silver and rainbow coins retain their original scoring or obstacle/speed behavior.
- A blue coin grants six moves of border wrapping. It does not prevent collision with the snake or a wall at the exit cell.
- A cyan shield coin grants a shield for up to 20 moves. It cancels one lethal move, restores the previous position, then disappears.
- An orange slow coin halves movement speed for eight moves.
- A purple poison coin removes two body segments, down to the head alone, and awards no score.

The `EffectManager` tracks remaining game moves. Picking up the same effect again refreshes its duration; different effects can run together. `GameSession` applies effects and scoring, while the JavaFX controller adjusts the timeline rate and draws the result. The active effects appear at the top of the game screen.

## Verification

The model tests cover map validation, level loading/reset, score targets, effect expiry and refresh, shield and ghost collisions, poison shortening, and coin selection. Run `./mvnw clean verify` after changes. The menu and HUD also need a manual visual check on a desktop with JavaFX.

## AI use

Codex assisted with the level, effect, coin, test and README implementation on the `codex/document-expansion` branch. The student should review and understand the retained code and record this assistance in the COMP2042 AI Use Declaration Form before submission.
