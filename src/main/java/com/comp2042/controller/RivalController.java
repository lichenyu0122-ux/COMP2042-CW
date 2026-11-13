package com.comp2042.controller;

import com.comp2042.model.Heading;
import com.comp2042.model.RivalAi;
import com.comp2042.model.RivalMatch;
import com.comp2042.model.RivalSnake;
import com.comp2042.view.RivalView;
import com.comp2042.view.ViewManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

/** Connects keyboard input and two independent snake clocks to the match. */
public final class RivalController {
    private final RivalMatch match;
    private final RivalView view;
    private final ViewManager viewManager;
    private final RivalAi ai = new RivalAi();
    private final Timeline playerClock;
    private final Timeline enemyClock;
    private Heading requested = Heading.RIGHT;
    private boolean turnedBeforeMove;
    private boolean running;

    public RivalController(RivalMatch match, RivalView view, ViewManager viewManager) {
        this.match = match;
        this.view = view;
        this.viewManager = viewManager;
        playerClock = new Timeline(new KeyFrame(Duration.seconds(1), event -> movePlayer()));
        enemyClock = new Timeline(new KeyFrame(Duration.seconds(1), event -> moveEnemy()));
        playerClock.setCycleCount(Animation.INDEFINITE);
        enemyClock.setCycleCount(Animation.INDEFINITE);
        updateRates();
    }

    public void handle(KeyEvent event) {
        KeyCode key = event.getCode();
        if (key == KeyCode.ESCAPE) {
            stop();
            viewManager.showMainMenu();
        } else if (key == KeyCode.TAB) {
            stop();
            match.reset();
            requested = Heading.RIGHT;
            turnedBeforeMove = false;
            updateRates();
            view.setPaused(false);
        } else if (key == KeyCode.P) {
            if (running) {
                stop();
                view.setPaused(true);
            } else if (match.winner() == null) {
                start();
            }
        } else if (key == KeyCode.PLUS) {
            viewManager.getGameStage().setFullScreen(true);
        } else if (key == KeyCode.MINUS) {
            viewManager.getGameStage().setFullScreen(false);
        } else {
            Heading next = headingFor(key);
            if (next != null && match.winner() == null) {
                Heading current = match.snake(RivalMatch.Side.PLAYER).heading();
                if (!turnedBeforeMove && !current.isOpposite(next) && next != current) {
                    requested = next;
                    turnedBeforeMove = true;
                }
                if (!running) {
                    start();
                }
            }
        }
    }

    public void stop() {
        playerClock.stop();
        enemyClock.stop();
        running = false;
    }

    private void start() {
        playerClock.play();
        enemyClock.play();
        running = true;
        view.setPaused(false);
    }

    private void movePlayer() {
        RivalSnake before = match.snake(RivalMatch.Side.PLAYER);
        match.move(RivalMatch.Side.PLAYER, requested);
        RivalSnake after = match.snake(RivalMatch.Side.PLAYER);
        requested = after == before ? after.heading() : Heading.RIGHT;
        turnedBeforeMove = false;
        updateAfterMove();
    }

    private void moveEnemy() {
        RivalSnake oldPlayer = match.snake(RivalMatch.Side.PLAYER);
        match.move(RivalMatch.Side.ENEMY, ai.choose(match));
        if (match.snake(RivalMatch.Side.PLAYER) != oldPlayer) {
            requested = Heading.RIGHT;
            turnedBeforeMove = false;
        }
        updateAfterMove();
    }

    private void updateAfterMove() {
        updateRates();
        view.render();
        if (match.winner() != null) {
            stop();
        }
    }

    private void updateRates() {
        double baseRate = match.level().movesPerSecond();
        playerClock.setRate(baseRate * (match.snake(RivalMatch.Side.PLAYER).speedBoosted() ? 1.5 : 1));
        enemyClock.setRate(baseRate * (match.snake(RivalMatch.Side.ENEMY).speedBoosted() ? 1.5 : 1));
    }

    static Heading headingFor(KeyCode key) {
        return switch (key) {
            case UP -> Heading.UP;
            case RIGHT -> Heading.RIGHT;
            case DOWN -> Heading.DOWN;
            case LEFT -> Heading.LEFT;
            default -> null;
        };
    }
}
