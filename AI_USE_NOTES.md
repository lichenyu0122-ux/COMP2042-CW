# AI assistance record

This is a working record for the COMP2042 AI Use Declaration Form. It does not replace the form or the student's own review.

Tool used: OpenAI Codex, 13 and 15 November 2026.

| Area | AI-assisted work | Files to review |
| --- | --- | --- |
| Level data | Parsed and validated text maps; added three English level files, menu selection and target scores | `model/Level.java`, `LevelParser.java`, `LevelCatalog.java`, `Game.java`, `GameSession.java`, `controller/MainMenuController.java`, `MainMenu.fxml`, `levels/` |
| Timed items | Added tick-based shield, slow and ghost effects; connected collisions, timeline speed and HUD | `model/TimedEffect.java`, `EffectManager.java`, `ShieldEffect.java`, `SlowEffect.java`, `GhostEffect.java`, `PowerUpFactory.java`, `Snake.java`, `controller/GameController.java`, `view/GameView.java` |
| Poison hazard | Added poison generation and two-segment shrink; kept a separate reward available | `model/PoisonCoin.java`, `Coin.java`, `CoinSpawnPolicy.java`, `GameSession.java`, `controller/GameController.java`, `view/GameView.java` |
| Verification and explanation | Added or updated JUnit tests, README and this record | `src/test/java/`, `README.md`, `AI_USE_NOTES.md` |

Before submission, the student should confirm which suggestions they kept, explain the move/pickup/effect flow in their own words, and submit the required declaration form.
