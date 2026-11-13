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
import com.comp2042.view.GameView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class GameController {
    private final GameSession session;
    private final GameView gameView;
    private final Game game;
    private final Stage stage;
    private final Obstacle obstacle;

    private final DirectionInput direction = new DirectionInput();
    private final Timeline animation;

    public GameController(GameSession session, GameView gameView, Stage stage) {
        this.session = session;
        this.gameView = gameView;
        this.game = session.game();
        this.stage = stage;
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

            if (direction.current() == KeyCode.LEFT) {
                snek.move('L');
            } else if (direction.current() == KeyCode.RIGHT) {
                snek.move('R');
            } else if (direction.current() == KeyCode.UP) {
                snek.move('U');
            } else if (direction.current() == KeyCode.DOWN) {
                snek.move('D');
            }

            if (!snek.getDead() && snek.ateCoin(coin)) {
                powerCoin();
                game.incScore();
                if (game.gameWon()) {
                    gameView.drawGrid(coin, snek, obstacle, gameView.gc, false, 0);
                    return;
                }
                newCoin();
            }

            direction.nextTick();

            if (!snek.getDead() || snek.hitSelf()) {
                gameView.drawGrid(session.coin(), snek, obstacle, gameView.gc, false, 0);
            }

            gameView.gameWon();
            gameView.gameOver(snek);
            gameView.updateScoreLabel();
        }
    }

    public void handle(KeyEvent event) {
        Snake snek = session.snake();
        Coin coin = session.coin();
        if (event.getCode().isArrowKey()) animation.play();

        KeyCode previousKey = direction.current();
        if (direction.tryTurn(event.getCode())) {
            gameView.turnDir(snek, previousKey, direction.current());
        }

        if (event.getCode().equals(KeyCode.P)) {
            animation.stop();
        }

        if (event.getCode().equals(KeyCode.TAB)) {
            reset();
            gameView.scoreLabel.setText("Score: " + game.getScorePoints());
        }

        if (event.getCode().equals(KeyCode.PLUS)) {
            gameView.scaleToFullscreen(true, stage, obstacle, snek, coin);
            gameView.gameOverLabel.setStyle("-fx-font-size: 50;");
            gameView.gameWonLabel.setStyle("-fx-font-size: 50;");
            gameView.scoreLabel.setStyle("-fx-font-size: 35;");
        }

        if (event.getCode().equals(KeyCode.MINUS)) {
            gameView.scaleToFullscreen(false, stage, obstacle, snek, coin);
            gameView.gameOverLabel.setStyle("-fx-font-size: 15;");
            gameView.gameWonLabel.setStyle("-fx-font-size: 15;");
            gameView.scoreLabel.setStyle("-fx-font-size: 15;");
        }

        if (event.getCode().equals(KeyCode.ESCAPE)) {
            MainMenuController mainMenuController = new MainMenuController();
            mainMenuController.getViewManager().getPrimaryStage().show();
            mainMenuController.getViewManager().getGameStage().close();
        }
    }

    private void reset() {

        animation.jumpTo(Duration.ZERO);
        animation.stop();

        gameView.resetTurns();
        session.reset();

        animation.setRate(game.getSpeed());
        direction.reset();

        gameView.gameWon();
        gameView.gameOver(session.snake());
        gameView.updateScoreLabel();

        gameView.drawGrid(session.coin(), session.snake(), obstacle, gameView.gc, false, 0);
    }

    private void powerCoin() {
        Snake snek = session.snake();
        Coin coin = session.coin();
        animation.setRate(game.getSpeed());
        snek.ignoreBorders(false);
        if (coin.getClass().equals(RainbowCoin.class)) {
            RainbowCoin tempCoin = (RainbowCoin) coin;
            animation.setRate(game.getSpeed() + tempCoin.getSpeedBonus());
        } else if (coin.getClass().equals(SilverCoin.class)){
            Random rnd = new Random();
            obstacle.newObstacles(snek, coin, rnd.nextInt(game.getWidth() * game.getHeight()/ 20) +3);
            game.decMaxScore(obstacle.getObstacles().size());
        } else if (coin.getClass().equals(BlueCoin.class)) {
            snek.ignoreBorders(true);
        }
        game.addPoints(coin.getPoints());
    }
    private void newCoin() {
        Snake snek = session.snake();
        boolean powerUp = game.isPowerUp();
        int roll = powerUp ? new Random().nextInt(12) : -1;
        boolean hasObstacles = !obstacle.getObstacles().isEmpty();

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
            case REGULAR ->
                    session.replaceCoin(new Coin(game.getWidth(), game.getHeight(), snek, obstacle));
        }
    }
}
