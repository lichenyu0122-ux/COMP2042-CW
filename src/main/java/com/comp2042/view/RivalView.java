package com.comp2042.view;

import com.comp2042.model.CoinSpawnPolicy;
import com.comp2042.model.RivalFood;
import com.comp2042.model.RivalLevel;
import com.comp2042.model.RivalMatch;
import com.comp2042.model.RivalSnake;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.awt.Point;
import java.util.List;

/** Draws the two-snake score race without storing game rules. */
public final class RivalView {
    private static final int CELL = 20;
    private static final Color PLAYER_COLOR = Color.web("#27A77A");
    private static final Color ENEMY_COLOR = Color.web("#B050A9");
    private final RivalMatch match;
    private final Canvas board = new Canvas(RivalLevel.WIDTH * CELL, RivalLevel.HEIGHT * CELL);
    private final Label scoreLabel = new Label();
    private final Label statusLabel = new Label();
    private boolean paused;

    public RivalView(RivalMatch match) {
        this.match = match;
    }

    public Scene createScene() {
        Label title = new Label(match.level().title());
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #182C34;");
        scoreLabel.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #182C34;");
        statusLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #52646D;");

        HBox heading = new HBox(title);
        heading.setAlignment(Pos.CENTER_LEFT);
        VBox top = new VBox(4, heading, scoreLabel, statusLabel);
        top.setPadding(new Insets(0, 0, 12, 0));

        StackPane gameArea = new StackPane(board);
        gameArea.setStyle("-fx-background-color: #E4EBE4; -fx-padding: 3px;");

        Label help = new Label("Arrows: move    P: pause    Tab: restart    Esc: menu");
        help.setStyle("-fx-font-size: 12px; -fx-text-fill: #52646D;");
        HBox footer = new HBox(help);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10, 0, 0, 0));

        BorderPane root = new BorderPane(gameArea);
        root.setTop(top);
        root.setBottom(footer);
        root.setPadding(new Insets(16));
        root.setStyle("-fx-background-color: #F6F8F3;");
        render();
        return new Scene(root, 446, 552);
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
        render();
    }

    public void render() {
        RivalSnake player = match.snake(RivalMatch.Side.PLAYER);
        RivalSnake enemy = match.snake(RivalMatch.Side.ENEMY);
        scoreLabel.setText("YOU  " + player.score() + "      RIVAL  " + enemy.score()
                + "      GOAL  " + RivalLevel.TARGET_SCORE);
        if (match.winner() == RivalMatch.Side.PLAYER) {
            statusLabel.setText("You win! Press Tab to play again.");
        } else if (match.winner() == RivalMatch.Side.ENEMY) {
            statusLabel.setText("Rival wins. Press Tab to try again.");
        } else if (paused) {
            statusLabel.setText("Paused. Press P to continue.");
        } else {
            statusLabel.setText("Green: you    Purple: rival    Gold dots: dropped score"
                    + powerText(player, enemy));
        }

        GraphicsContext gc = board.getGraphicsContext2D();
        gc.setFill(Color.web("#F9F8E9"));
        gc.fillRect(0, 0, board.getWidth(), board.getHeight());
        gc.setStroke(Color.web("#E7E8D8"));
        gc.setLineWidth(1);
        for (int x = 0; x <= RivalLevel.WIDTH; x++) {
            gc.strokeLine(x * CELL + 0.5, 0, x * CELL + 0.5, board.getHeight());
        }
        for (int y = 0; y <= RivalLevel.HEIGHT; y++) {
            gc.strokeLine(0, y * CELL + 0.5, board.getWidth(), y * CELL + 0.5);
        }

        gc.setFill(Color.web("#56616A"));
        for (Point obstacle : match.obstacles()) {
            gc.fillRoundRect(obstacle.x * CELL + 2, obstacle.y * CELL + 2,
                    CELL - 4, CELL - 4, 5, 5);
        }
        for (RivalFood food : match.foods()) {
            drawFood(gc, food);
        }
        drawSnake(gc, enemy, ENEMY_COLOR);
        drawSnake(gc, player, PLAYER_COLOR);
    }

    private static String powerText(RivalSnake player, RivalSnake enemy) {
        StringBuilder text = new StringBuilder();
        if (player.speedBoosted() || player.canWrap()) {
            text.append("    Your power: ");
            if (player.speedBoosted()) text.append("speed ");
            if (player.canWrap()) text.append("wrap");
        }
        if (enemy.speedBoosted() || enemy.canWrap()) {
            text.append("    Rival powered");
        }
        return text.toString();
    }

    private static void drawFood(GraphicsContext gc, RivalFood food) {
        Color color = food.dropped() ? Color.web("#E5A514") : switch (food.type()) {
            case REGULAR -> Color.web("#E95C4B");
            case GOLD -> Color.web("#F0C233");
            case RAINBOW -> Color.web("#8669D8");
            case BLUE -> Color.web("#44A7DD");
            case SILVER -> Color.web("#B6BEC3");
        };
        Point point = food.position();
        gc.setFill(color);
        gc.fillOval(point.x * CELL + 4, point.y * CELL + 4, CELL - 8, CELL - 8);
        gc.setStroke(Color.web("#6B5540"));
        gc.strokeOval(point.x * CELL + 4, point.y * CELL + 4, CELL - 8, CELL - 8);
        if (food.dropped()) {
            gc.setFill(Color.web("#3B2B14"));
            gc.setFont(Font.font("Consolas", 10));
            gc.fillText(Integer.toString(food.points()), point.x * CELL + 8, point.y * CELL + 16);
        }
    }

    private static void drawSnake(GraphicsContext gc, RivalSnake snake, Color color) {
        List<Point> segments = snake.segments();
        for (int index = segments.size() - 1; index >= 0; index--) {
            Point point = segments.get(index);
            gc.setFill(index == 0 ? color.darker() : color);
            gc.fillRoundRect(point.x * CELL + 2, point.y * CELL + 2,
                    CELL - 4, CELL - 4, 7, 7);
        }
        Point head = snake.head();
        gc.setFill(Color.WHITE);
        gc.fillOval(head.x * CELL + 7, head.y * CELL + 7, 4, 4);
        gc.fillOval(head.x * CELL + 14, head.y * CELL + 7, 4, 4);
    }
}
