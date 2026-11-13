package com.comp2042.view;

import com.comp2042.model.Coin;
import com.comp2042.model.BlueCoin;
import com.comp2042.model.Game;
import com.comp2042.model.GoldCoin;
import com.comp2042.model.Obstacle;
import com.comp2042.model.RainbowCoin;
import com.comp2042.model.SilverCoin;
import com.comp2042.model.Snake;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

public class GameView {

    private static final Color GRID_COLOR = Color.web("feffe4");
    private static final Color OBSTACLE_COLOR = Color.BLACK;
    private final Map<Class<?>, Image> coinImages = new HashMap<>();
    private final ImageView imageHead = new ImageView(new Image("resources/views/res/snakehead.png"));
    private final ImageView imageBody = new ImageView(new Image("resources/views/res/snakebody.png"));
    private final ImageView imageTurn = new ImageView(new Image("resources/views/res/snaketurnleft.png"));
    private final ImageView imageTail = new ImageView(new Image("resources/views/res/snaketail.png"));

    public Canvas gridCanvas;
    public GraphicsContext gc;

    public Label gameOverLabel;
    public Label gameWonLabel;
    public Label scoreLabel;
    private boolean gameOverVis = false;
    private boolean gameWonVis = false;

    private Game game;
    private double initX;
    private double initY;
    private double fullScale = 1;
    private boolean fullsize = false;
    private int[][] turnArray;

    public GameView(Game game) {
        this.game = game;
        this.turnArray = new int[game.getWidth()][game.getHeight()];
    }

    public void makeScene(Scene scene, Snake snek, Coin coin) {
        Scene gameScene = scene;
        gridCanvas = new Canvas(gameScene.getWidth(), gameScene.getHeight());
        initX = scene.getWidth();
        initY = scene.getHeight();
        gc = gridCanvas.getGraphicsContext2D();
        drawGrid(coin, snek, gc);

        gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setId("gameOverLabel");
        String css = "resources/views/styling.css";
        gameOverLabel.getStylesheets().add(css);
        gameOverLabel.setVisible(false);

        gameWonLabel = new Label("YOU WON!");
        gameWonLabel.setId("gameWonLabel");
        gameWonLabel.getStylesheets().add(css);
        gameWonLabel.setVisible(false);

        scoreLabel = new Label("Score: " + game.getScorePoints());
        scoreLabel.setId("scoreLabel");
        scoreLabel.getStylesheets().add(css);
        scoreLabel.setFont(new Font("Arial", 20));
        scoreLabel.setVisible(true);
    }

    public void scaleToFullscreen(boolean fullScreen, Stage primaryStage, Obstacle obstacle, Snake snek, Coin coin) {
        primaryStage.setFullScreen(fullScreen);

        double xScaling = Screen.getPrimary().getVisualBounds().getHeight() / initY;
        double yScaling = Screen.getPrimary().getVisualBounds().getWidth() / initX;

        if (fullScreen && !fullsize) {
            if (xScaling < yScaling) {
                fullScale = xScaling;
            } else {
                fullScale = yScaling;
            }
            gridCanvas.setWidth(gridCanvas.getWidth() * fullScale);
            gridCanvas.setHeight(gridCanvas.getHeight() * fullScale);
            fullsize = true;
        } else if (!fullScreen) {
            fullScale = 1;
            gridCanvas.setWidth(initX);
            gridCanvas.setHeight(initY);
            fullsize = false;
        }

        gameOverLabel.setFont(new Font("Arial", 25 * fullScale));
        gameWonLabel.setFont(new Font("Arial", 25 * fullScale));
        drawGrid(coin, snek, obstacle, gc, false, 0);
    }

    public void drawGrid(Coin coin, Snake snek, Obstacle obstacle, GraphicsContext gc, boolean a, int b) {
        gc.setFill(GRID_COLOR);
        gc.fillRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());

        gc.drawImage(coinImage(coin), (coin.getPos().x * game.getMultiplier()) * fullScale, (coin.getPos().y * game.getMultiplier()) * fullScale, game.getMultiplier() * fullScale - 3 * fullScale, game.getMultiplier() * fullScale - 3 * fullScale);

        int bodyIndex = 0;
        for (Point point : snek.getBody()) {
            drawBody(snek, point, bodyIndex);
            bodyIndex++;
        }

        drawHead(snek);

        gc.setFill(OBSTACLE_COLOR);
        for (Point point : obstacle.getObstacles()) {
            gc.fillRect(point.x * game.getMultiplier() * fullScale, point.y * game.getMultiplier() * fullScale, game.getMultiplier() * fullScale, game.getMultiplier() * fullScale);
        }
    }

    private void drawGrid(Coin coin, Snake snek, GraphicsContext gc) {
        gc.setFill(GRID_COLOR);
        gc.fillRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());

        gc.drawImage(coinImage(coin), (coin.getPos().x * game.getMultiplier()) * fullScale, (coin.getPos().y * game.getMultiplier()) * fullScale, game.getMultiplier() * fullScale - 3 * fullScale, game.getMultiplier() * fullScale - 3 * fullScale);

        int bodyIndex = 0;
        for (Point point : snek.getBody()) {
            drawBody(snek, point, bodyIndex);
            bodyIndex++;
        }

        drawHead(snek);
    }

    private Image coinImage(Coin coin) {
        return coinImages.computeIfAbsent(coin.getClass(), type -> {
            String sprite = "redcoin.png";
            if (type == GoldCoin.class) {
                sprite = "goldcoin.png";
            } else if (type == SilverCoin.class) {
                sprite = "silvercoin.png";
            } else if (type == BlueCoin.class) {
                sprite = "bluecoin.png";
            } else if (type == RainbowCoin.class) {
                sprite = "rainbowcoin.png";
            }
            return new Image("resources/views/res/" + sprite);
        });
    }

    private void drawHead(Snake snek) {
        imageHead.setRotate(snek.getSnakeDir());
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Image rotatedImage = imageHead.snapshot(params, null);
        gc.drawImage(rotatedImage, (snek.getHead().x * game.getMultiplier()) * fullScale, (snek.getHead().y * game.getMultiplier()) * fullScale, game.getMultiplier() * fullScale * fullScale, game.getMultiplier() * fullScale * fullScale);
    }

    private void drawBody(Snake snek, Point point, int index) {
        Image rotatedImage;
        if (snek.getBody().size() <= 1) {
            imageTail.setRotate(snek.getSnakeDir());
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            rotatedImage = imageTail.snapshot(params, null);
        } else if (index == 0) {
            int rotateTail;
            if (snek.getBody().get(1).getX() == snek.getBody().get(0).getX() && snek.getBody().get(1).getY() == snek.getBody().get(0).getY() - 1) {
                rotateTail = 0;
            } else if (snek.getBody().get(1).getX() == snek.getBody().get(0).getX() && snek.getBody().get(1).getY() == snek.getBody().get(0).getY() + 1) {
                rotateTail = 180;
            } else if (snek.getBody().get(1).getX() == snek.getBody().get(0).getX() - 1 && snek.getBody().get(1).getY() == snek.getBody().get(0).getY()) {
                rotateTail = 270;
            } else {
                rotateTail = 90;
            }
            imageTail.setRotate(rotateTail);
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            rotatedImage = imageTail.snapshot(params, null);
        } else if (index > 0 && (turnArray[(int) snek.getBody().get(index).getX()][(int) snek.getBody().get(index).getY()] != 0)) {
            imageTurn.setRotate(90 * turnArray[(int) snek.getBody().get(index).getX()][(int) snek.getBody().get(index).getY()]);
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            rotatedImage = imageTurn.snapshot(params, null);
        } else {
            imageBody.setRotate(snek.getBodyDir(index));
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            rotatedImage = imageBody.snapshot(params, null);
        }

        if (turnArray[(int) snek.getBody().get(0).getX()][(int) snek.getBody().get(0).getY()] != 0) {
            turnArray[(int) snek.getBody().get(0).getX()][(int) snek.getBody().get(0).getY()] = 0;
        }

        gc.drawImage(rotatedImage, (point.x * game.getMultiplier()) * fullScale, (point.y * game.getMultiplier()) * fullScale, game.getMultiplier() * fullScale * fullScale, game.getMultiplier() * fullScale * fullScale);
    }

    public void turnDir(Snake snek, KeyCode lastKey, KeyCode key) {
        if ((lastKey == KeyCode.LEFT && key == KeyCode.UP) || (lastKey == KeyCode.DOWN && key == KeyCode.RIGHT)) {
            turnArray[(int) snek.getHead().getX()][(int) snek.getHead().getY()] = 2;
        } else if ((lastKey == KeyCode.LEFT && key == KeyCode.DOWN) || (lastKey == KeyCode.UP && key == KeyCode.RIGHT)) {
            turnArray[(int) snek.getHead().getX()][(int) snek.getHead().getY()] = 3;
        } else if ((lastKey == KeyCode.RIGHT && key == KeyCode.UP) || (lastKey == KeyCode.DOWN && key == KeyCode.LEFT)) {
            turnArray[(int) snek.getHead().getX()][(int) snek.getHead().getY()] = 1;
        } else if ((lastKey == KeyCode.UP && key == KeyCode.LEFT) || (lastKey == KeyCode.RIGHT && key == KeyCode.DOWN)) {
            turnArray[(int) snek.getHead().getX()][(int) snek.getHead().getY()] = 4;
        } else turnArray[(int) snek.getHead().getX()][(int) snek.getHead().getY()] = 0;
    }

    public void resetTurns() {
        turnArray = new int[game.getWidth()][game.getHeight()];
    }

    public void gameOver(Snake snek) {
        if (snek.getDead() && !gameOverVis) {
            gameOverVis = true;
            gameOverLabel.setVisible(true);
        } else if (!snek.getDead() && gameOverVis) {
            gameOverLabel.setVisible(false);
            gameOverVis = false;
        }
    }

    public void gameWon() {
        if (game.gameWon() && !gameWonVis) {
            gameWonVis = true;
            gameWonLabel.setVisible(true);
        } else if (!game.gameWon() && gameWonVis) {
            gameWonVis = false;
            gameWonLabel.setVisible(false);
        }
    }

    public void updateScoreLabel() {
        scoreLabel.setText("Score: " + game.getScorePoints());
    }

}
