package com.comp2042.model;

/**
 * Kinds of move-limited effects supported by the game.
 */
public enum EffectType {
    /** Absorbs one lethal collision. */
    SHIELD,
    /** Reduces the movement rate. */
    SLOW,
    /** Crosses borders and walls. */
    GHOST
}
