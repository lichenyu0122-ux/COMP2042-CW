package com.comp2042.model;

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

    public Game(int x, int y, boolean powerUp, char difficulty) {
        this(x, y, powerUp, difficulty, 0, 0);
    }

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

    public boolean gameWon() {
        return score >= maxScore;
    }

    public void incScore() {
        score++;
    }

    public void decMaxScore(int num) {
        if (!fixedTarget) this.maxScore -= (num - 1);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void resetScore() {
        score = 0;
        scorePoints = 0;
        maxScore = initialMaxScore;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void addPoints(int n) {
        scorePoints += n;
    }

    public int getScorePoints() {
        return scorePoints;
    }

    public int getTargetScore() { return maxScore; }
    public int getScore() { return score; }

    public boolean isPowerUp() {
        return powerUp;
    }

    public double getSpeed() {
        return speed;
    }

    public boolean isNoBorder() {
        return noBorder;
    }
}
