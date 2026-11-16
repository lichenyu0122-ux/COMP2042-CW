# COMP2042 Snake

## GitHub

https://github.com/lichenyu0122-ux/COMP2042-CW

## Compilation instructions

1. Install JDK 25 and select it as the project SDK. The code targets Java 21 bytecode, but JavaFX 25.0.1 contains Java 23 bytecode, so a Java 21 runtime cannot launch this dependency. JDK 25 is the version used to verify this project.
2. From the project root, run `.\mvnw.cmd clean verify` on Windows or `./mvnw clean verify` on macOS or Linux. The Maven Wrapper downloads Maven and the dependencies on first use. This builds the application and runs the JUnit tests.
3. Run `.\mvnw.cmd javafx:run` on Windows or `./mvnw javafx:run` on macOS or Linux. In IntelliJ, open the project directory containing `pom.xml` and set the Maven runner JDK to 25.

## Design and Javadoc

[Design.pdf](Design.pdf) shows the classes and their relationships. The generated API pages are in [Javadoc/index.html](Javadoc/index.html). After changing Java code, run `.\mvnw.cmd javadoc:javadoc` on Windows or `./mvnw javadoc:javadoc` elsewhere, then copy the contents of `target/reports/apidocs/` into `Javadoc/` so `index.html` stays at the folder root.

## Implemented and working properly

- **Maintenance:** The code is grouped into `model`, `controller`, and `view` packages. Direction input rejects immediate reversals; reset clears score, snake state, effects, turns, poison, and generated obstacles. Coin placement avoids the snake and walls. Git line endings are normalized by `.gitattributes`.
- **Three selectable levels:** `src/main/resources/levels/` contains Grassland (no preset walls, 10 pickups, 200 ms base tick), Maze (walls, 15 pickups, 180 ms), and Hourglass (narrow gates, 20 pickups, 150 ms). These are progressively faster at the same difficulty. A level is described by `name`, `target`, `speed`, `cols`, `rows`, and a dot/hash map. Add a `.level` file and register it in `levels/index.txt` to add another level.
- **Difficulty:** The menu offers Easy, Normal, and Hard. On the three levels they apply 0.8x, 1.0x, and 1.25x movement rate. All level borders are lethal without Ghost, including Easy, so the Ghost power-up has a distinct purpose. The legacy game constructor without a level retains its original Easy wraparound behavior.
- **Rewards and penalties:** Regular food is worth 5 points, Gold 15, Rainbow temporarily increases movement rate, and Silver adds obstacles when no wall or poison is present. Silver is therefore unavailable in Maze and Hourglass, and while a poison coin is on Grassland. A purple `!` poison coin appears alongside normal food, removes up to two body segments while preserving at least one, and deducts 10 points without going below zero. Poison does not reduce target progress.
- **Timed effects:** Blue `G` grants 20 moves of Ghost, allowing passage through preset walls and board edges, but not through the snake's body. Cyan `S` grants one Shield for up to 20 moves; after it absorbs a lethal hit, movement pauses until the player selects a valid turn. Orange Slow halves movement rate for eight moves. Collecting the same effect refreshes its duration; different effects can coexist. The HUD shows the level, target, points, and active effect durations.
- **Controls:** Arrow keys turn, `P` pauses or resumes, `Tab` restarts, and `Esc` returns to the menu. The controller maps `+` to full screen and `-` back to windowed mode.

## Implemented but not working properly

No reproducible defect is currently known in the implemented level and effect rules. The JUnit suite covers the core logic; a final manual playthrough in the submission environment is still needed to check rendering and keyboard focus. The menu still obtains `ViewManager` through `Main`'s static reference; this works in the current startup path but makes isolated menu testing harder. Changing FXML construction was deferred because it would require a separate UI regression check.

## Features not implemented

The selected scope is the three levels and their item effects. No planned gameplay feature in that scope is unfinished.

## New Java classes

| Location | Classes and purpose |
| --- | --- |
| `src/main/java/com/comp2042/controller/` | `DirectionInput` validates turns and handles the shield recovery turn. |
| `src/main/java/com/comp2042/model/` | `Level`, `LevelParser`, and `LevelCatalog` hold, validate, and load map-driven stages. `GameSession` owns one round and reset. `CoinSpawnPolicy` selects item types separately from the UI. |
| `src/main/java/com/comp2042/model/` | `EffectType`, `TimedEffect`, `EffectManager`, `PowerUpFactory`, `GhostEffect`, `ShieldEffect`, and `SlowEffect` define and manage move-based effects. |
| `src/main/java/com/comp2042/model/` | `PoisonCoin`, `ShieldCoin`, and `SlowCoin` add the penalty and two new pickups. |

## Modified Java classes

| Location | Change and reason |
| --- | --- |
| `src/main/java/com/comp2042/Main.java` and `view/ViewManager.java` | Start and switch between menu and game windows using the reorganized packages. |
| `src/main/java/com/comp2042/controller/MainMenuController.java` | Offer difficulty and level choices and create the selected round. |
| `src/main/java/com/comp2042/controller/GameController.java` | Connect input, timer, item spawning, and view updates to `GameSession`; use one random generator and stop after a shield save until a turn. |
| `src/main/java/com/comp2042/view/GameView.java` | Render level walls, distinct item markers, scores, and effect notices. |
| `src/main/java/com/comp2042/model/Game.java` and `model/Snake.java` | Apply level targets and speeds, handle collisions and Ghost wrapping, and limit poison shrinkage. |
| `src/main/java/com/comp2042/model/Obstacle.java` and `model/Coin.java` | Restore level walls on reset and choose only free cells for new rewards and obstacles. |
| `src/main/java/com/comp2042/model/BlueCoin.java`, `GoldCoin.java`, `RainbowCoin.java`, and `SilverCoin.java` | Keep the original coin variants working in the new package structure and scoring system. |

## Unexpected problems and resolutions

- The original Maven Wrapper download URL and a Windows path containing parentheses prevented startup; the wrapper URL and launcher handling were corrected.
- JavaFX FXML version and runtime mismatches prevented the menu from loading; dependency versions and FXML declarations were aligned.
- Ghost initially failed to pass walls because collision handling and effect duration did not agree; movement and effect tests now cover wall crossing and expiry.
- A Shield could be consumed without giving the player a chance to turn; it now pauses the game on the safe cell. The `P` key resumes only ordinary pauses.
- Windows CRLF changes obscured meaningful Git diffs; `.gitattributes` now specifies line endings for source and resources.

## Verification

Run `.\mvnw.cmd clean verify javadoc:javadoc` on Windows or `./mvnw clean verify javadoc:javadoc` elsewhere. The latest run passed all 53 JUnit tests, with no Javadoc warnings.

| Test area | Test classes | Main checks |
| --- | --- | --- |
| Input and difficulty | `DirectionInputTest`, `GameDifficultyTest` | Reversal prevention, shield recovery, level speed and border rules. |
| Levels and restart | `LevelParserTest`, `GameSessionTest` | Map validation, target progress, wall restoration and reset. |
| Coins and effects | `CoinSpawnPolicyTest`, `PoisonCoinTest`, `EffectManagerTest` | Item selection, poison limits, effect duration and collisions. |
| Movement and scoring | `SnakeCollisionTest`, `ObstacleTest`, `CoinTest`, `GameScoringTest` | Wall and self collisions, free-cell placement, obstacle cap and score reset. |

Before submission, play all three levels in the packaged project and check the Ghost, Shield, Slow and Poison display and keyboard focus. The automated tests do not check JavaFX rendering.
