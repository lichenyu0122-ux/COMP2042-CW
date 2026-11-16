package com.comp2042.model;

import java.awt.Point;
import java.util.Objects;

/**
 * Owns one round of snake, food, walls, and effects; reset recreates its mutable state.
 */
public final class GameSession {
    private static final int POISON_POINT_PENALTY = 10;
    private final Game game;
    private final Obstacle obstacle;
    private final Level level;
    private final EffectManager effects = new EffectManager();
    private Snake snake;
    private Coin coin;
    private PoisonCoin poisonCoin;

    /**
     * Creates a round without a preset level.
     *
     * @param game the game rules and score
     */
    public GameSession(Game game) {
        this(game, null);
    }

    /**
     * Creates a round and loads its preset walls when a level is supplied.
     *
     * @param game the game rules and score
     * @param level the selected level, or null for a game without a preset level
     * @throws NullPointerException if game is null
     * @throws IllegalArgumentException if game dimensions differ from the level
     */
    public GameSession(Game game, Level level) {
        this.game = Objects.requireNonNull(game);
        this.level = level;
        if (level != null && (level.columns() != game.getWidth() || level.rows() != game.getHeight())) {
            throw new IllegalArgumentException("Game dimensions differ from level");
        }
        obstacle = new Obstacle(game.getWidth(), game.getHeight());
        reset();
    }

    /**
     * The same Game object that holds this round's score, target, and difficulty.
     *
     * @return the game configuration and score
     */
    public Game game() {
        return game;
    }

    /**
     * The walls used for collision checks and coin placement in this round.
     *
     * @return the round obstacle collection
     */
    public Obstacle obstacle() {
        return obstacle;
    }

    /**
     * The chosen map, or null when the game uses no level file.
     *
     * @return the level, or null for a game without a preset level
     */
    public Level level() { return level; }
    /**
     * The active Shield, Slow, and Ghost effects for this round.
     *
     * @return the effect manager for this round
     */
    public EffectManager effects() { return effects; }

    /**
     * Outcome of a move before pickup processing.
     */
    public enum MoveResult {
        /** Movement completed without a lethal collision. */
        MOVED,
        /** A shield restored the state before a lethal collision. */
        SHIELDED,
        /** The snake died during this move. */
        DEAD
    }
    /**
     * Kind of item reached after a successful move.
     */
    public enum Pickup {
        /** No item was reached. */
        NONE,
        /** A reward was collected. */
        REWARD,
        /** Poison was collected. */
        POISON
    }

    /**
     * Moves the snake, applies Ghost and Shield, and advances effect durations.
     *
     * @param direction U, D, L, or R movement direction
     * @return MOVED for a safe move, SHIELDED if a shield saved it, or DEAD
     * @throws IllegalArgumentException if direction is not U, D, L, or R
     */
    public MoveResult move(char direction) {
        snake.setGhostMode(effects.isActive(EffectType.GHOST));
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

    /**
     * Checks the head after a safe move. Reward coins advance the target; poison shortens the snake and deducts points.
     *
     * @return the collected pickup kind, or NONE
     */
    public Pickup finishMove() {
        Coin touched = poisonCoin != null && snake.getHead().equals(poisonCoin.getPos())
                ? poisonCoin : coin;
        if (!snake.ateCoin(touched)) return Pickup.NONE;
        if (touched == poisonCoin) {
            snake.shrink(2);
            game.deductPoints(POISON_POINT_PENALTY);
            poisonCoin = null;
            return Pickup.POISON;
        }
        collectCoin();
        return Pickup.REWARD;
    }

    /**
     * Credits the current reward and activates its matching effect, if any.
     */
    public void collectCoin() {
        if (coin instanceof ShieldCoin) effects.activate(PowerUpFactory.create(EffectType.SHIELD));
        else if (coin instanceof SlowCoin) effects.activate(PowerUpFactory.create(EffectType.SLOW));
        else if (coin instanceof BlueCoin) effects.activate(PowerUpFactory.create(EffectType.GHOST));
        game.incScore();
        game.addPoints(coin.getPoints());
    }

    /**
     * The snake that moves during this round; reset replaces it with a new one.
     *
     * @return the current snake
     */
    public Snake snake() {
        return snake;
    }

    /**
     * The reward coin on the board, separate from any poison coin.
     *
     * @return the current reward coin
     */
    public Coin coin() {
        return coin;
    }

    /**
     * The poison coin on the board, if one has spawned.
     *
     * @return the poison coin, or null
     */
    public PoisonCoin poisonCoin() { return poisonCoin; }

    /**
     * Places poison only on a cell free of the snake, walls, and reward.
     *
     * @param nextPoison the poison pickup to place
     * @throws NullPointerException if the poison coin is null
     * @throws IllegalArgumentException if the position is occupied
     */
    public void placePoisonCoin(PoisonCoin nextPoison) {
        Objects.requireNonNull(nextPoison);
        Point position = nextPoison.getPos();
        if (coin.getPos().equals(position) || snake.getHead().equals(position)
                || snake.getBody().contains(position) || obstacle.getObstacles().contains(position)) {
            throw new IllegalArgumentException("Poison must occupy a free cell distinct from the reward");
        }
        poisonCoin = nextPoison;
    }

    /**
     * Replaces the reward and relocates poison if the positions overlap.
     *
     * @param nextCoin the new reward pickup
     * @throws NullPointerException if the reward is null
     * @throws IllegalArgumentException if the replacement is a PoisonCoin
     */
    public void replaceCoin(Coin nextCoin) {
        if (nextCoin instanceof PoisonCoin) throw new IllegalArgumentException("Poison is a separate hazard");
        coin = Objects.requireNonNull(nextCoin);
        if (poisonCoin != null && coin.getPos().equals(poisonCoin.getPos())) {
            poisonCoin = new PoisonCoin(game.getWidth(), game.getHeight(), snake, obstacle, coin);
        }
    }

    /**
     * Resets scores and effects and creates a fresh snake and reward with the level walls.
     */
    public void reset() {
        game.resetScore();
        effects.clear();
        obstacle.reset();
        if (level != null) obstacle.load(level);
        snake = new Snake(game.getWidth(), game.getHeight(), game.isNoBorder(), obstacle);
        coin = new Coin(game.getWidth(), game.getHeight(), snake, obstacle);
        poisonCoin = null;
    }
}
