package com.comp2042.model;

import java.util.Objects;

/** Holds the mutable state for one game and starts a fresh round. */
public final class GameSession {
    private final Game game;
    private final Obstacle obstacle;
    private Snake snake;
    private Coin coin;

    public GameSession(Game game) {
        this.game = Objects.requireNonNull(game);
        obstacle = new Obstacle(game.getWidth(), game.getHeight());
        reset();
    }

    public Game game() {
        return game;
    }

    public Obstacle obstacle() {
        return obstacle;
    }

    public Snake snake() {
        return snake;
    }

    public Coin coin() {
        return coin;
    }

    public void replaceCoin(Coin nextCoin) {
        coin = Objects.requireNonNull(nextCoin);
    }

    public void reset() {
        game.resetScore();
        obstacle.reset();
        snake = new Snake(game.getWidth(), game.getHeight(), game.isNoBorder(), obstacle);
        coin = new Coin(game.getWidth(), game.getHeight(), snake, obstacle);
    }
}
