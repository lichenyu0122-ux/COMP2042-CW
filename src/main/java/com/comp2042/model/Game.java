package com.comp2042.model;

public class Game {
    private int width;
    private int height;
    private int score;
    private int maxScore;
    private double multiplier;
    private int scorePoints;
    private double speed;
    private boolean powerUp;
    private boolean noBorder;

    public Game(int x, int y, boolean powerUp, char difficulty) {
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

        if (height > 50) {
            multiplier = 10;
        } else {
            multiplier = 20;
        }
    }

    public boolean gameWon() {
        return score == maxScore;
    }

    public void incScore() {
        score++;
    }

    public void decMaxScore(int num) {
        this.maxScore -= (num - 1);
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
        maxScore = width * height - 2;
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
