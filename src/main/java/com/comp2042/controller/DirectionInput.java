package com.comp2042.controller;

import javafx.scene.input.KeyCode;

/** Tracks direction changes between game ticks. */
final class DirectionInput {
    private KeyCode current = KeyCode.LEFT;
    private boolean changedThisTick;

    KeyCode current() {
        return current;
    }

    boolean tryTurn(KeyCode next) {
        if (changedThisTick || !isValidDirection(current, next)) {
            return false;
        }
        current = next;
        changedThisTick = true;
        return true;
    }

    void nextTick() {
        changedThisTick = false;
    }

    void reset() {
        current = KeyCode.LEFT;
        changedThisTick = false;
    }

    static boolean isValidDirection(KeyCode current, KeyCode next) {
        if (next == null || !next.isArrowKey() || next == current) {
            return false;
        }
        return switch (current) {
            case LEFT -> next != KeyCode.RIGHT;
            case RIGHT -> next != KeyCode.LEFT;
            case UP -> next != KeyCode.DOWN;
            case DOWN -> next != KeyCode.UP;
            default -> false;
        };
    }
}
