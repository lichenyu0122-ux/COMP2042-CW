# COMP2042 Snake

GitHub repository: https://github.com/lichenyu0122-ux/COMP2042-CW

## Run

Use JDK 25. On Windows, run `.\mvnw.cmd javafx:run` from the repository root; on macOS or Linux, run `./mvnw javafx:run`. Replace `javafx:run` with `clean verify` to run the tests.

In IntelliJ IDEA, open the Git checkout that contains this README and `pom.xml`, import it as a Maven project, select JDK 25, and run the Maven `javafx:run` goal. The extension is developed on the `codex/level-effects-document` branch.

## Play

Choose a difficulty and a level in the main menu. Use the arrow keys to steer, `P` to pause, `Tab` to restart, `Esc` to return to the menu, and `+`/`-` to toggle full screen.

The three levels are loaded from `src/main/resources/levels/`: Grassland has no fixed walls and a target of 10 food pickups; Maze has static passages and a target of 15; Hourglass has two narrow central gates and a target of 20. Level files set the map, target and base tick speed (150, 180, and 200 ms respectively). Easy runs at 80% of that rate, Normal at 100%, and Hard at 125%. Border wrapping in a level requires the Ghost effect, including on Easy.

To add a level, create a `.level` file and add its filename to `levels/index.txt`; the menu reads that index without a Java code change. `#` means wall and `.` means free space. `LevelParser` validates dimensions, characters, the start position and the target. The bundled maps have tests proving every free cell can be reached from the starting cell.

All menus, level names, score labels and effect names displayed in the game are in English.

## Items and penalties

- Red, gold, silver and rainbow coins retain their original scoring or obstacle/speed behavior.
- A blue coin grants six moves of border wrapping. It does not prevent collision with the snake or a wall at the exit cell.
- A cyan shield coin grants a shield for up to 20 moves. It cancels one lethal move, restores the previous position, then disappears.
- An orange slow coin halves movement speed for eight moves.
- A purple poison coin appears as an extra hazard alongside a normal reward coin. It removes two body segments, down to the head alone, and awards no score. The reward remains available if the player avoids the poison.

The `EffectManager` tracks remaining game moves. Picking up the same effect again refreshes its duration; different effects can run together. `GameSession` applies collisions, pickups, effects and scoring; `GameController` handles input and the JavaFX timeline; `GameView` draws the result. The active effects appear at the top of the game screen. Legacy silver coins can add obstacles on Grassland after play begins.

## Design and changed classes

`Level` stores immutable map data, `LevelParser` validates the text format, and `LevelCatalog` loads the index. `TimedEffect`, its three subclasses, `EffectManager`, and `PowerUpFactory` keep effect rules independent of JavaFX. `PoisonCoin`, `ShieldCoin`, and `SlowCoin` identify the new pickups. `GameSession` owns the level, snake, reward, poison hazard, and effects. `Game` applies each level's target and difficulty-adjusted speed. `Snake` can shrink and can undo a lethal move when the shield is consumed. `Coin` chooses uniformly from free cells and fails clearly if none exist. `CoinSpawnPolicy` includes the new item rolls. The menu controller and FXML select levels; `GameView` displays them and the active effects.

## Verification

The model tests cover map validation and connectivity, level loading/reset, score targets, effect expiry and refresh, shield and ghost collisions, poison shortening, a still-available reward while poison is present, and coin selection. Run `clean verify` after changes. On a desktop, select each level, check the level and effect HUD, collect each colored item, verify that poison leaves the reward in place, and use `Tab` to verify reset. The optional adaptive-speed strategy from the design proposal is not implemented.

## AI use

Codex assisted with the level, effect, coin, test and README implementation on the `codex/level-effects-document` branch. The student should review and understand the retained code and record this assistance in the COMP2042 AI Use Declaration Form before submission. A file-by-file record is in `AI_USE_NOTES.md`.
