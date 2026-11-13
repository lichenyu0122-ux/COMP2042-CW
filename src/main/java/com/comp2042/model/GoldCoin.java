package com.comp2042.model;

public class GoldCoin extends Coin {

    public GoldCoin(int x, int y, Snake snek, Obstacle obstacle) {
        super(x, y, snek, obstacle);
    }

    public int getPoints() {
        int points = 15;
        return points;
    }
}
