package com.comp2042.controller;

import com.comp2042.model.BlueCoin;
import com.comp2042.model.Coin;
import com.comp2042.model.CoinSpawnPolicy;
import com.comp2042.model.Game;
import com.comp2042.model.GameSession;
import com.comp2042.model.GoldCoin;
import com.comp2042.model.Obstacle;
import com.comp2042.model.RainbowCoin;
import com.comp2042.model.SilverCoin;
import com.comp2042.model.Snake;
import com.comp2042.model.ShieldCoin;
import com.comp2042.model.SlowCoin;
import com.comp2042.model.PoisonCoin;
import com.comp2042.view.GameView;
import com.comp2042.view.ViewManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

import java.util.Random;

/**
 * Connects keyboard input and the animation timer to the round state and game view.
 */
public class GameController {
    private static final int POISON_NOTICE_TICKS = 12;
    private static final String POISON_NOTICE = "POISON: -2 body, -10 pts";
    private static final String SHIELD_NOTICE = "SHIELD SAVED YOU - TURN TO CONTINUE";
    private final GameSession session;
    private final GameView gameView;
    private final Game game;
    private final ViewManager viewManager;
    private final Obstacle obstacle;

    private final DirectionInput direction = new DirectionInput();
    private final Random random = new Random();
    private final Timeline animation;
    private double rainbowBonus;
    private int poisonNoticeTicks;

    /**
     * Creates the timer and connects the round, view, and window manager.
     *
     * @param session the round state
     * @param gameView the board view
     * @param viewManager the window manager
     */
    public GameController(GameSession session, GameView gameView, ViewManager viewManager) {
        this.session = session;
        this.gameView = gameView;
        this.game = session.game();
        this.viewManager = viewManager;
        this.obstacle = session.obstacle();
        animation = new Timeline(new KeyFrame(Duration.seconds(1), event -> tick()));
        animation.setCycleCount(Animation.INDEFINITE);
        animation.setRate(game.getSpeed());
    }

    private void tick() {
        Snake snek = session.snake();
        Coin coin = session.coin();
        if (snek.getDead()) {
            return;
        } else {

            char move = switch (direction.current()) {
                case LEFT -> 'L';
                case RIGHT -> 'R';
                case UP -> 'U';
                case DOWN -> 'D';
                default -> throw new IllegalStateException("No direction selected");
            };
            GameSession.MoveResult result = session.move(move);
            GameSession.Pickup pickup = result == GameSession.MoveResult.MOVED
                    ? session.finishMove() : GameSession.Pickup.NONE;

            if (result == GameSession.MoveResult.SHIELDED) {
                direction.requireTurnAfterShield();
                animation.pause();
            }

            if (pickup == GameSession.Pickup.REWARD) {
                powerCoin();
                if (game.gameWon()) {
                    gameView.drawGrid(coin, session.poisonCoin(), snek, obstacle, gameView.gc);
                    gameView.gameWon();
                    gameView.updateScoreLabel();
                    gameView.updateEffectLabel(session.effects());
                    animation.stop();
                    return;
                }
                newCoin();
            } else if (pickup == GameSession.Pickup.POISON) {
                poisonNoticeTicks = POISON_NOTICE_TICKS;
            }
            animation.setRate((game.getSpeed() + rainbowBonus) * session.effects().speedMultiplier());

            direction.nextTick();

            if (!snek.getDead() || snek.hitSelf()) {
                gameView.drawGrid(session.coin(), session.poisonCoin(), snek, obstacle, gameView.gc);
            }

            gameView.gameWon();
            gameView.gameOver(snek);
            gameView.updateScoreLabel();
            gameView.updateEffectLabel(session.effects(), direction.needsTurnAfterShield()
                    ? SHIELD_NOTICE
                    : poisonNoticeTicks > 0 ? POISON_NOTICE : "");
            if (poisonNoticeTicks > 0) poisonNoticeTicks--;
        }
    }

    /**
     * Processes movement, pause, restart, display-size, and menu keys.
     *
     * @param event the pressed key event
     */
    public void handle(KeyEvent event) {
        Snake snek = session.snake();
        Coin coin = session.coin();
        if (event.getCode().isArrowKey()
                && animation.getStatus() == Animation.Status.STOPPED
                && !snek.getDead() && !game.gameWon()) {
            animation.play();
        }

        KeyCode previousKey = direction.current();
        boolean recoveringFromShield = direction.needsTurnAfterShield();
        if (direction.tryTurn(event.getCode())) {
            gameView.turnDir(snek, previousKey, direction.current());
            if (recoveringFromShield && !snek.getDead() && !game.gameWon()) {
                gameView.updateEffectLabel(session.effects());
                animation.play();
            }
        }

        if (event.getCode().equals(KeyCode.P) && !direction.needsTurnAfterShield()) {
            if (animation.getStatus() == Animation.Status.RUNNING) {
                animation.pause();
            } else if (animation.getStatus() == Animation.Status.PAUSED
                    && !snek.getDead() && !game.gameWon()) {
                animation.play();
            }
        }

        if (event.getCode().equals(KeyCode.TAB)) {
            reset();
            gameView.updateScoreLabel();
        }

        if (event.getCode().equals(KeyCode.PLUS)) {
            gameView.scaleToFullscreen(true, viewManager.getGameStage(), obstacle,
                    snek, coin, session.poisonCoin());
            gameView.gameOverLabel.setStyle("-fx-font-size: 50;");
            gameView.gameWonLabel.setStyle("-fx-font-size: 50;");
            gameView.scoreLabel.setStyle("-fx-font-size: 35;");
        }

        if (event.getCode().equals(KeyCode.MINUS)) {
            gameView.scaleToFullscreen(false, viewManager.getGameStage(), obstacle,
                    snek, coin, session.poisonCoin());
            gameView.gameOverLabel.setStyle("-fx-font-size: 15;");
            gameView.gameWonLabel.setStyle("-fx-font-size: 15;");
            gameView.scoreLabel.setStyle("-fx-font-size: 15;");
        }

        if (event.getCode().equals(KeyCode.ESCAPE)) {
            animation.stop();
            viewManager.showMainMenu();
        }
    }

    private void reset() {

        animation.jumpTo(Duration.ZERO);
        animation.stop();

        gameView.resetTurns();
        session.reset();

        rainbowBonus = 0;
        poisonNoticeTicks = 0;
        animation.setRate(game.getSpeed());
        direction.reset();

        gameView.gameWon();
        gameView.gameOver(session.snake());
        gameView.updateScoreLabel();
        gameView.updateEffectLabel(session.effects());

        gameView.drawGrid(session.coin(), session.poisonCoin(), session.snake(), obstacle, gameView.gc);
    }

    private void powerCoin() {
        Snake snek = session.snake();
        Coin coin = session.coin();
        rainbowBonus = 0;
        if (coin.getClass().equals(RainbowCoin.class)) {
            RainbowCoin tempCoin = (RainbowCoin) coin;
            rainbowBonus = tempCoin.getSpeedBonus();
        } else if (coin.getClass().equals(SilverCoin.class)){
            obstacle.newObstacles(snek, coin, random.nextInt(game.getWidth() * game.getHeight()/ 20) +3);
            game.decMaxScore(obstacle.getObstacles().size());
        }
    }
    private void newCoin() {
        Snake snek = session.snake();
        boolean powerUp = game.isPowerUp();
        int roll = powerUp ? random.nextInt(20) : -1;
        boolean silverBlocked = session.poisonCoin() != null || !obstacle.getObstacles().isEmpty();

        CoinSpawnPolicy.CoinType type =
                CoinSpawnPolicy.select(powerUp, roll, silverBlocked);

        switch (type) {
            case GOLD ->
                    session.replaceCoin(new GoldCoin(game.getWidth(), game.getHeight(), snek, obstacle));
            case RAINBOW ->
                    session.replaceCoin(new RainbowCoin(game.getWidth(), game.getHeight(), snek, obstacle));
            case BLUE ->
                    session.replaceCoin(new BlueCoin(game.getWidth(), game.getHeight(), snek, obstacle));
            case SILVER ->
                    session.replaceCoin(new SilverCoin(game.getWidth(), game.getHeight(), snek, obstacle));
            case SHIELD ->
                    session.replaceCoin(new ShieldCoin(game.getWidth(), game.getHeight(), snek, obstacle));
            case SLOW ->
                    session.replaceCoin(new SlowCoin(game.getWidth(), game.getHeight(), snek, obstacle));
            case POISON ->
            {
                session.replaceCoin(new Coin(game.getWidth(), game.getHeight(), snek, obstacle));
                if (session.poisonCoin() == null) {
                    session.placePoisonCoin(new PoisonCoin(game.getWidth(), game.getHeight(),
                            snek, obstacle, session.coin()));
                }
            }
            case REGULAR ->
                    session.replaceCoin(new Coin(game.getWidth(), game.getHeight(), snek, obstacle));
        }
    }
}
