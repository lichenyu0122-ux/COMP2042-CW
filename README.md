# COMP2042 Snake coursework

Repository: https://github.com/lichenyu0122-ux/COMP2042-CW

This project maintains the supplied JavaFX Snake game and adds two rival stages. The maintenance baseline is tagged `v1-maintenance`; later commits contain the new stages.

## Build and run

The project was tested on Windows 11 with JDK 25 and Maven Wrapper 3.9.11. JavaFX 25.0.1 and JUnit are fetched by Maven. From the repository root:

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd clean package
.\mvnw.cmd javafx:run
```

On macOS or Linux, use `./mvnw` in place of `.\mvnw.cmd`. The package command creates `target/snake-javafx.jar`. The JavaFX Maven command is the tested way to launch the application.

## Playable modes

The menu offers **Classic**, **Rival 1: Meadow**, and **Rival 2: Crossroads**. Classic retains the Easy, Normal, and Hard difficulty choices. The two rival stages share the same 20 by 20 board but have different obstacle layouts. Rival 1 starts with four obstacles at 4.5 moves per second; Rival 2 starts with eight obstacles at 5.5 moves per second. Both are slower than the original Normal difficulty (8.5 moves per second).

In Rival mode, the green player snake and purple computer snake compete for the same beans. The score display shows both totals. The first snake to reach **100 points** wins. A normal bean is worth 5 points and a gold bean is worth 15. The computer searches for reachable beans while avoiding walls, obstacles, and snake bodies. Its moving body can block and kill the player.

If a snake hits the other snake's body, a wall, an obstacle, or itself, it loses its score and respawns. A head-to-head collision kills both snakes. Each dead snake leaves nearby beans whose total value equals its score immediately before death. Dropped beans are worth up to 5 points each, with a smaller final bean when needed. Both snakes may collect them.

The original power-up types also appear in Rival mode and affect whichever snake eats them:

| Bean | Effect |
| --- | --- |
| Gold | 15 points instead of 5 |
| Rainbow | 5 points and 1.5 times movement speed for 12 moves |
| Blue | 5 points and wraparound borders for 16 moves |
| Silver | 5 points and 1 or 2 new obstacles, depending on the stage; obstacle growth is capped |

**Controls:** Arrow keys steer and start a rival match. `P` pauses or resumes. `Tab` restarts the current stage. `Esc` returns to the menu. `+` and `-` toggle fullscreen. The same arrow, pause, restart, and menu controls remain available in Classic mode.

## Maintenance and design

- `model`, `controller`, and `view` packages separate game rules from JavaFX rendering. The model package has no JavaFX imports, so rules can be tested without a display.
- The direction reversal bug, obstacle placement and capacity, and game reset threshold were corrected. `.gitattributes` keeps source text on LF line endings.
- `DirectionInput` keeps one accepted turn per game tick. `GameSession` owns the Classic round's snake, coin, obstacles, and reset state.
- New Rival model classes are `Heading`, `RivalLevel`, `RivalSnake`, `RivalFood`, `RivalMatch`, and `RivalAi` in `src/main/java/com/comp2042/model/`. `RivalMatch` resolves scores, collisions, respawns, dropped beans, and powers; `RivalAi` selects a route to food.
- New JavaFX classes are `RivalController` in `src/main/java/com/comp2042/controller/` and `RivalView` in `src/main/java/com/comp2042/view/`. The controller owns two movement clocks; the view draws the board and score. `MainMenuController` and `MainMenu.fxml` were modified to select a mode.

## Tests and current status

JUnit tests cover the original scoring and difficulty rules, coin selection, direction input, Classic snake collisions, obstacle generation, round reset, Rival collisions in both directions, exact dropped-score totals, 100-point victory, powers, AI safety, and long matches in both stages. Run them with `.\mvnw.cmd clean test`. The current suite has **40 passing tests**. The JavaFX menu and Rival 1 gameplay have also been opened for a visual check.

No known failing feature is being claimed as complete. Automated end-to-end keyboard and rendering tests are not included; the JavaFX interaction check is manual. The final coursework `Design.pdf`, generated Javadoc, and demonstration video have not yet been prepared. The final project should also be tested on another computer before submission.

One unexpected maintenance issue was that loading JavaFX images in the `Snake` constructor prevented headless JUnit collision tests. Moving rendering assets into the view removed that dependency.

Generative AI assistance used for assessed work must be declared in the course's AI Use Declaration Form. The student should review and be able to explain all submitted code.
