package com.comp2042.model;

import java.util.Objects;

/** Holds the mutable state for one game and starts a fresh round. */
public final class GameSession {
    private final Game game;
    private final Obstacle obstacle;
    private final Level level;
    private final EffectManager effects = new EffectManager();
    private Snake snake;
    private Coin coin;

    public GameSession(Game game) {
        this(game, null);
    }

    public GameSession(Game game, Level level) {
        this.game = Objects.requireNonNull(game);
        this.level = level;
        if (level != null && (level.columns() != game.getWidth() || level.rows() != game.getHeight())) {
            throw new IllegalArgumentException("Game dimensions differ from level");
        }
        obstacle = new Obstacle(game.getWidth(), game.getHeight());
        reset();
    }

    public Game game() {
        return game;
    }

    public Obstacle obstacle() {
        return obstacle;
    }

    public Level level() { return level; }
    public EffectManager effects() { return effects; }

    public enum MoveResult { MOVED, SHIELDED, DEAD }

    public MoveResult move(char direction) {
        snake.ignoreBorders(effects.isActive(EffectType.GHOST));
        snake.move(direction);
        MoveResult result = MoveResult.MOVED;
        if (snake.getDead()) {
            if (effects.consumeShield()) {
                snake.undoDeath();
                result = MoveResult.SHIELDED;
            } else result = MoveResult.DEAD;
        }
        effects.tick();
        return result;
    }

    /** Applies the picked-up item after movement, so it keeps its full tick duration. */
    public void collectCoin() {
        if (coin instanceof ShieldCoin) effects.activate(PowerUpFactory.create(EffectType.SHIELD));
        else if (coin instanceof SlowCoin) effects.activate(PowerUpFactory.create(EffectType.SLOW));
        else if (coin instanceof BlueCoin) effects.activate(PowerUpFactory.create(EffectType.GHOST));
        game.incScore();
        game.addPoints(coin.getPoints());
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
        effects.clear();
        obstacle.reset();
        if (level != null) obstacle.load(level);
        snake = new Snake(game.getWidth(), game.getHeight(), game.isNoBorder(), obstacle);
        coin = new Coin(game.getWidth(), game.getHeight(), snake, obstacle);
    }
}
