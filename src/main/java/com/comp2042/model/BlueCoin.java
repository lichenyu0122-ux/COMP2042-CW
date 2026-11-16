package com.comp2042.model;

import java.awt.Point;

public class BlueCoin extends Coin {

    public BlueCoin(int x, int y, Snake snek, Obstacle obstacle) {
        super(x, y, snek, obstacle);
    }

    BlueCoin(Point position) {
        super(position);
    }

}
