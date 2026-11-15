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

public class GameController {
    private final GameSession session;
    private final GameView gameView;
    private final Game game;
    private final ViewManager viewManager;
    private final Obstacle obstacle;

    private final DirectionInput direction = new DirectionInput();
    private final Timeline animation;
    private double rainbowBonus;
    private int poisonNoticeTicks;

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
                poisonNoticeTicks = 12;
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
                    ? "SHIELD SAVED YOU - TURN TO CONTINUE"
                    : poisonNoticeTicks > 0 ? "POISON: -2 body, -10 pts" : "");
            if (poisonNoticeTicks > 0) poisonNoticeTicks--;
        }
    }

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
            Random rnd = new Random();
            obstacle.newObstacles(snek, coin, rnd.nextInt(game.getWidth() * game.getHeight()/ 20) +3);
            game.decMaxScore(obstacle.getObstacles().size());
        }
    }
    private void newCoin() {
        Snake snek = session.snake();
        boolean powerUp = game.isPowerUp();
        int roll = powerUp ? new Random().nextInt(20) : -1;
        boolean hasObstacles = session.poisonCoin() != null || !obstacle.getObstacles().isEmpty();

        CoinSpawnPolicy.CoinType type =
                CoinSpawnPolicy.select(powerUp, roll, hasObstacles);

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
