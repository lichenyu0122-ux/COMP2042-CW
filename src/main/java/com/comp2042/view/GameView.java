package com.comp2042.view;

import com.comp2042.model.Coin;
import com.comp2042.model.BlueCoin;
import com.comp2042.model.Game;
import com.comp2042.model.Level;
import com.comp2042.model.GoldCoin;
import com.comp2042.model.Obstacle;
import com.comp2042.model.RainbowCoin;
import com.comp2042.model.SilverCoin;
import com.comp2042.model.Snake;
import com.comp2042.model.ShieldCoin;
import com.comp2042.model.SlowCoin;
import com.comp2042.model.PoisonCoin;
import com.comp2042.model.EffectManager;
import com.comp2042.model.EffectType;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

/**
 * Draws the board, pickups, snake, score, and timed-effect notices using JavaFX.
 */
public class GameView {

    private static final Color GRID_COLOR = Color.web("feffe4");
    private static final Color OBSTACLE_COLOR = Color.BLACK;
    private final Map<Class<?>, Image> coinImages = new HashMap<>();
    private final ImageView imageHead = new ImageView(new Image("resources/views/res/snakehead.png"));
    private final ImageView imageBody = new ImageView(new Image("resources/views/res/snakebody.png"));
    private final ImageView imageTurn = new ImageView(new Image("resources/views/res/snaketurnleft.png"));
    private final ImageView imageTail = new ImageView(new Image("resources/views/res/snaketail.png"));

    /**
     * Canvas containing the game board.
     */
    public Canvas gridCanvas;
    /**
     * Drawing context for the board canvas.
     */
    public GraphicsContext gc;

    /**
     * Loss message overlay.
     */
    public Label gameOverLabel;
    /**
     * Win message overlay.
     */
    public Label gameWonLabel;
    /**
     * Level progress and points display.
     */
    public Label scoreLabel;
    /**
     * Timed effects and temporary notice display.
     */
    public Label effectLabel;
    private boolean gameOverVis = false;
    private boolean gameWonVis = false;

    private Game game;
    private final Level level;
    private double initX;
    private double initY;
    private double fullScale = 1;
    private boolean fullsize = false;
    private int[][] turnArray;

    /**
     * Creates a view for a game without an associated level.
     *
     * @param game the game rules and score
     */
    public GameView(Game game) {
        this(game, null);
    }

    /**
     * Creates a view that can show the selected level in the score display.
     *
     * @param game the game rules and score
     * @param level the selected level, or null for a game without a preset level
     */
    public GameView(Game game, Level level) {
        this.game = game;
        this.level = level;
        this.turnArray = new int[game.getWidth()][game.getHeight()];
    }

    /**
     * Creates board and status controls and draws the initial state.
     *
     * @param snek the current snake
     * @param coin the current coin
     * @param poison the separate poison coin, or null
     * @param obstacle the current wall state
     */
    public void makeScene(Snake snek, Coin coin, PoisonCoin poison, Obstacle obstacle) {
        gridCanvas = new Canvas(game.getWidth() * game.getMultiplier(),
                game.getHeight() * game.getMultiplier());
        initX = gridCanvas.getWidth();
        initY = gridCanvas.getHeight();
        gc = gridCanvas.getGraphicsContext2D();
        drawGrid(coin, poison, snek, obstacle, gc);

        gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setId("gameOverLabel");
        String css = "resources/views/styling.css";
        gameOverLabel.getStylesheets().add(css);
        gameOverLabel.setVisible(false);

        gameWonLabel = new Label("YOU WON!");
        gameWonLabel.setId("gameWonLabel");
        gameWonLabel.getStylesheets().add(css);
        gameWonLabel.setVisible(false);

        scoreLabel = new Label(scoreText());
        scoreLabel.setId("scoreLabel");
        scoreLabel.getStylesheets().add(css);
        scoreLabel.setFont(new Font("Arial", 15));
        scoreLabel.setStyle("-fx-font-size: 15px; -fx-background-color: #feffe4;");
        scoreLabel.setPrefWidth(initX);
        scoreLabel.setMinHeight(32);
        scoreLabel.setMaxHeight(32);
        scoreLabel.setAlignment(Pos.CENTER);
        scoreLabel.setVisible(true);
        effectLabel = new Label("");
        effectLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #174656; -fx-background-color: #feffe4;");
        effectLabel.setMouseTransparent(true);
    }

    /**
     * Resizes the board and labels and redraws the current state.
     *
     * @param fullScreen whether to enter full-screen mode
     * @param primaryStage the stage supplied by JavaFX
     * @param obstacle the current wall state
     * @param snek the current snake
     * @param coin the current coin
     * @param poison the separate poison coin, or null
     */
    public void scaleToFullscreen(boolean fullScreen, Stage primaryStage, Obstacle obstacle,
                                  Snake snek, Coin coin, PoisonCoin poison) {
        primaryStage.setFullScreen(fullScreen);

        double hudHeight = fullScreen ? 48 : 32;
        scoreLabel.setMinHeight(hudHeight);
        scoreLabel.setMaxHeight(hudHeight);
        double xScaling = (Screen.getPrimary().getVisualBounds().getHeight() - hudHeight) / initY;
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
        drawGrid(coin, poison, snek, obstacle, gc);
    }

    /**
     * Redraws walls, both pickup types, and the snake on the supplied canvas context.
     *
     * @param coin the current coin
     * @param poison the separate poison coin, or null
     * @param snek the current snake
     * @param obstacle the current wall state
     * @param gc the canvas drawing context
     */
    public void drawGrid(Coin coin, PoisonCoin poison, Snake snek, Obstacle obstacle, GraphicsContext gc) {
        gc.setFill(GRID_COLOR);
        gc.fillRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());

        gc.setFill(OBSTACLE_COLOR);
        for (Point point : obstacle.getObstacles()) {
            gc.fillRect(point.x * game.getMultiplier() * fullScale, point.y * game.getMultiplier() * fullScale,
                    game.getMultiplier() * fullScale, game.getMultiplier() * fullScale);
        }

        drawCoin(coin);
        if (poison != null) drawCoin(poison);

        int bodyIndex = 0;
        for (Point point : snek.getBody()) {
            drawBody(snek, point, bodyIndex);
            bodyIndex++;
        }

        drawHead(snek);

    }

    private void drawCoin(Coin coin) {
        double x = coin.getPos().x * game.getMultiplier() * fullScale;
        double y = coin.getPos().y * game.getMultiplier() * fullScale;
        double size = (game.getMultiplier() - 3) * fullScale;
        if (coin instanceof ShieldCoin || coin instanceof SlowCoin || coin instanceof PoisonCoin) {
            gc.setFill(coin instanceof ShieldCoin ? Color.DEEPSKYBLUE
                    : coin instanceof SlowCoin ? Color.ORANGE : Color.PURPLE);
            gc.fillOval(x, y, size, size);
        } else gc.drawImage(coinImage(coin), x, y, size, size);

        String marker = coin instanceof BlueCoin ? "G"
                : coin instanceof ShieldCoin ? "S"
                : coin instanceof PoisonCoin ? "!" : null;
        if (marker != null) {
            gc.save();
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 12 * fullScale));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.setLineWidth(Math.max(1, fullScale));
            gc.setStroke(Color.BLACK);
            gc.strokeText(marker, x + size / 2, y + size / 2);
            gc.setFill(Color.WHITE);
            gc.fillText(marker, x + size / 2, y + size / 2);
            gc.restore();
        }
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

    /**
     * Records the corner sprite to use at the snake head when it turns.
     *
     * @param snek the current snake
     * @param lastKey the previous arrow key
     * @param key the newly accepted arrow key
     */
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

    /**
     * Clears stored corner sprite positions after a restart.
     */
    public void resetTurns() {
        turnArray = new int[game.getWidth()][game.getHeight()];
    }

    /**
     * Shows or hides the loss label according to snake state.
     *
     * @param snek the current snake
     */
    public void gameOver(Snake snek) {
        if (snek.getDead() && !gameOverVis) {
            gameOverVis = true;
            gameOverLabel.setVisible(true);
        } else if (!snek.getDead() && gameOverVis) {
            gameOverLabel.setVisible(false);
            gameOverVis = false;
        }
    }

    /**
     * Shows or hides the win label according to target progress.
     */
    public void gameWon() {
        if (game.gameWon() && !gameWonVis) {
            gameWonVis = true;
            gameWonLabel.setVisible(true);
        } else if (!game.gameWon() && gameWonVis) {
            gameWonVis = false;
            gameWonLabel.setVisible(false);
        }
    }

    /**
     * Refreshes the level progress and point display.
     */
    public void updateScoreLabel() {
        scoreLabel.setText(scoreText());
    }

    /**
     * Refreshes effect durations without an additional notice.
     *
     * @param effects the currently active effects
     */
    public void updateEffectLabel(EffectManager effects) {
        updateEffectLabel(effects, "");
    }

    /**
     * Refreshes effect durations and an optional notice.
     *
     * @param effects the currently active effects
     * @param notice an optional HUD notice; empty means none
     */
    public void updateEffectLabel(EffectManager effects, String notice) {
        StringBuilder text = new StringBuilder();
        if (!notice.isBlank()) text.append(notice);
        for (EffectType type : EffectType.values()) {
            int remaining = effects.remaining(type);
            if (remaining > 0) {
                if (!text.isEmpty()) text.append('\n');
                text.append(type.name()).append(' ').append(remaining).append(" moves: ");
                text.append(switch (type) {
                    case SHIELD -> "blocks one hit";
                    case SLOW -> "half speed";
                    case GHOST -> "pass walls";
                });
            }
        }
        effectLabel.setText(text.toString());
    }

    private String scoreText() {
        if (level == null) return "Score: " + game.getScorePoints();
        return level.name() + " " + game.getScore() + "/" + game.getTargetScore()
                + " | " + game.getScorePoints() + " pts";
    }

}
