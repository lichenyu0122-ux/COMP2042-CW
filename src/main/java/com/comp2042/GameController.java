package com.comp2042;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class GameController {
    private Snake snek;
    private Coin coin;
    private GameView gameView;
    private Game game;
    private Stage stage;
    private Obstacle obstacle;

    public static KeyCode key = KeyCode.LEFT;
    public static KeyCode lastKey = KeyCode.LEFT;
    private boolean keyPressed = false;

    public GameController(Snake snek, Coin coin, Game game, GameView gameView, Obstacle obstacle, Stage stage) {
        this.snek = snek;
        this.coin = coin;
        this.gameView = gameView;
        this.game = game;
        this.stage = stage;

        this.obstacle = obstacle;
        animation.setCycleCount(Animation.INDEFINITE);
        animation.setRate(game.getSpeed());
    }

    private final Timeline animation = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
        if (snek.getDead()) {
            return;
        } else {

            if (key.equals(KeyCode.LEFT)) {
                snek.move('L');
            } else if (key.equals(KeyCode.RIGHT)) {
                snek.move('R');
            } else if (key.equals(KeyCode.UP)) {
                snek.move('U');
            } else if (key.equals(KeyCode.DOWN)) {
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

            keyPressed = false;

            if (!snek.getDead() || snek.hitSelf()) {
                gameView.drawGrid(coin, snek, obstacle, gameView.gc, false, 0);
            }

            gameView.gameWon();
            gameView.gameOver(snek);
            gameView.updateScoreLabel();
        }
    }));

    public void handle(KeyEvent event) {
        if (event.getCode().isArrowKey()) animation.play();

        if (isValidDirection(key, event.getCode()) && !keyPressed) {
            lastKey = key;
            key = event.getCode();
            keyPressed = true;
            gameView.turnDir(snek);
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

    static boolean isValidDirection(KeyCode current, KeyCode next) {
        if (next == null || !next.isArrowKey() || next == current) {
            return false;
        }
        return switch (current) {
            case LEFT -> next != KeyCode.RIGHT;
            case RIGHT -> next != KeyCode.LEFT;
            case UP -> next != KeyCode.DOWN;
            case DOWN -> next != KeyCode.UP;
            default -> false;
        };
    }

    private void reset() {

        animation.jumpTo(Duration.ZERO);
        animation.stop();

        snek = new Snake(game.getWidth(), game.getHeight(), game.isNoBorder(), obstacle);
        coin = new Coin(game.getWidth(), game.getHeight(), snek, obstacle);

        animation.setRate(game.getSpeed());
        key = KeyCode.LEFT;
        keyPressed = false;
        game.resetScore();
        obstacle.reset();

        gameView.gameWon();
        gameView.gameOver(snek);
        gameView.updateScoreLabel();

        gameView.drawGrid(coin, snek, obstacle, gameView.gc, false, 0);
    }

    private void powerCoin() {
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
        boolean powerUp = game.isPowerUp();
        int roll = powerUp ? new Random().nextInt(12) : -1;
        boolean hasObstacles = !obstacle.getObstacles().isEmpty();

        CoinSpawnPolicy.CoinType type =
                CoinSpawnPolicy.select(powerUp, roll, hasObstacles);

        switch (type) {
            case GOLD ->
                    coin = new GoldCoin(game.getWidth(), game.getHeight(), snek, obstacle);
            case RAINBOW ->
                    coin = new RainbowCoin(game.getWidth(), game.getHeight(), snek, obstacle);
            case BLUE ->
                    coin = new BlueCoin(game.getWidth(), game.getHeight(), snek, obstacle);
            case SILVER ->
                    coin = new SilverCoin(game.getWidth(), game.getHeight(), snek, obstacle);
            case REGULAR ->
                    coin = new Coin(game.getWidth(), game.getHeight(), snek, obstacle);
        }
    }
}
