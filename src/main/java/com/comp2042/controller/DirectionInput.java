package com.comp2042.controller;

import javafx.scene.input.KeyCode;

/**
 * Accepts at most one valid turn per tick and requires a new turn after a shield save.
 */
final class DirectionInput {
    /** Starts with a leftward direction and no pending turn. */
    DirectionInput() {
    }
    private KeyCode current = KeyCode.LEFT;
    private boolean changedThisTick;
    private boolean needsTurnAfterShield;

    /**
     * Reads the direction used for the next movement tick.
     *
     * @return the current arrow key
     */
    KeyCode current() {
        return current;
    }

    /**
     * Accepts a nonreversing arrow turn if no turn was accepted this tick.
     *
     * @param next the proposed arrow direction
     * @return true when the turn was accepted
     */
    boolean tryTurn(KeyCode next) {
        if (changedThisTick || !isValidDirection(current, next)) {
            return false;
        }
        current = next;
        changedThisTick = true;
        needsTurnAfterShield = false;
        return true;
    }

    /**
     * Requires a valid new turn before movement resumes after a shield save.
     */
    void requireTurnAfterShield() {
        needsTurnAfterShield = true;
    }

    /**
     * Checks whether a shield save is waiting for a new direction.
     *
     * @return true while a recovery turn is required
     */
    boolean needsTurnAfterShield() {
        return needsTurnAfterShield;
    }

    /**
     * Allows one new direction change in the next movement tick.
     */
    void nextTick() {
        changedThisTick = false;
    }

    /**
     * Restores the initial left direction and clears pending turn state.
     */
    void reset() {
        current = KeyCode.LEFT;
        changedThisTick = false;
        needsTurnAfterShield = false;
    }

    /**
     * Rejects non-arrow, unchanged, and immediately reversed directions.
     *
     * @param current the current arrow direction
     * @param next the proposed arrow direction
     * @return true if the proposed direction can follow the current one
     */
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
