package com.comp2042.model;

/**
 * Stores board dimensions, difficulty, target progress, and awarded points for one round.
 */
public class Game {
    private int width;
    private int height;
    private int score;
    private int maxScore;
    private final int initialMaxScore;
    private final boolean fixedTarget;
    private double multiplier;
    private int scorePoints;
    private double speed;
    private boolean powerUp;
    private boolean noBorder;

    /**
     * Creates a game with a board-sized target and difficulty defaults.
     *
     * @param x the board width in cells
     * @param y the board height in cells
     * @param powerUp whether special pickups are enabled
     * @param difficulty E, N, or H difficulty code
     */
    public Game(int x, int y, boolean powerUp, char difficulty) {
        this(x, y, powerUp, difficulty, 0, 0);
    }

    /**
     * Creates a game with either a board-sized target or a fixed level target and tick speed.
     *
     * @param x the board width in cells
     * @param y the board height in cells
     * @param powerUp whether special pickups are enabled
     * @param difficulty E, N, or H difficulty code
     * @param targetScore fixed reward target, or zero for the board-sized default
     * @param speedMillis base tick duration, or zero for difficulty defaults
     * @throws IllegalArgumentException if the target and speed are inconsistent or the difficulty is unsupported
     */
    public Game(int x, int y, boolean powerUp, char difficulty, int targetScore, int speedMillis) {
        if (targetScore < 0 || speedMillis < 0 || (targetScore == 0) != (speedMillis == 0)) {
            throw new IllegalArgumentException("Level target and speed must both be positive");
        }
        switch (difficulty) {
            case 'E' -> {
                speed = 8.5;
                noBorder = true;
            }
            case 'N' -> {
                speed = 8.5;
                noBorder = false;
            }
            case 'H' -> {
                speed = 13;
                noBorder = false;
            }
            default -> throw new IllegalArgumentException(
                    "Unsupported difficulty: " + difficulty
            );
        }

        this.powerUp = powerUp;
        width = x;
        height = y;
        maxScore = x * y - 2;
        fixedTarget = targetScore > 0;
        if (fixedTarget) maxScore = targetScore;
        initialMaxScore = maxScore;
        if (speedMillis > 0) {
            double difficultyRate = switch (difficulty) {
                case 'E' -> 0.8;
                case 'N' -> 1.0;
                case 'H' -> 1.25;
                default -> throw new IllegalStateException("Difficulty was not validated");
            };
            speed = 1000.0 / speedMillis * difficultyRate;
            noBorder = false; // Ghost is the only way to cross a level boundary.
        }

        if (height > 50) {
            multiplier = 10;
        } else {
            multiplier = 20;
        }
    }

    /**
     * Checks whether the number of collected rewards reached the target.
     *
     * @return true once target progress is complete
     */
    public boolean gameWon() {
        return score >= maxScore;
    }

    /**
     * Advances the collected-reward count by one.
     */
    public void incScore() {
        score++;
    }

    /**
     * Reduces the dynamic target after obstacles are added; fixed level targets are unchanged.
     *
     * @param num the number of added obstacles used to adjust a dynamic target
     */
    public void decMaxScore(int num) {
        if (!fixedTarget) this.maxScore -= (num - 1);
    }

    /**
     * Reads the board width.
     *
     * @return the number of columns
     */
    public int getWidth() {
        return width;
    }

    /**
     * Reads the board height.
     *
     * @return the number of rows
     */
    public int getHeight() {
        return height;
    }

    /**
     * Clears points and progress and restores the initial target.
     */
    public void resetScore() {
        score = 0;
        scorePoints = 0;
        maxScore = initialMaxScore;
    }

    /**
     * Reads the display size of one board cell.
     *
     * @return the cell size in pixels
     */
    public double getMultiplier() {
        return multiplier;
    }

    /**
     * Adds a pickup point value to the point total.
     *
     * @param n the points to add
     */
    public void addPoints(int n) {
        scorePoints += n;
    }

    /**
     * Subtracts a penalty without reducing points below zero.
     *
     * @param amount the nonnegative point penalty or requested obstacle count
     * @throws IllegalArgumentException if the penalty is negative
     */
    public void deductPoints(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Penalty must be non-negative");
        scorePoints = Math.max(0, scorePoints - amount);
    }

    /**
     * Reads the awarded point total, including penalties.
     *
     * @return the current nonnegative point total
     */
    public int getScorePoints() {
        return scorePoints;
    }

    /**
     * Reads the number of rewards required to win.
     *
     * @return the current target count
     */
    public int getTargetScore() { return maxScore; }
    /**
     * Reads how many reward coins have been collected.
     *
     * @return the reward count toward the target
     */
    public int getScore() { return score; }

    /**
     * Checks whether special pickups are enabled.
     *
     * @return true when special pickups can spawn
     */
    public boolean isPowerUp() {
        return powerUp;
    }

    /**
     * Reads the movement timer rate derived from difficulty and level speed.
     *
     * @return moves per second
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Checks legacy border wrapping; level games disable it.
     *
     * @return true if crossing an edge wraps the snake
     */
    public boolean isNoBorder() {
        return noBorder;
    }
}
