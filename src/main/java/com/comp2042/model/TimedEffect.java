package com.comp2042.model;

/**
 * Base class for effects whose remaining duration is counted in moves.
 */
public abstract class TimedEffect {
    private int remaining;

    /**
     * Creates an effect with a positive move duration.
     *
     * @param duration the positive duration measured in moves
     * @throws IllegalArgumentException if duration is not positive
     */
    protected TimedEffect(int duration) {
        if (duration <= 0) throw new IllegalArgumentException("Duration must be positive");
        remaining = duration;
    }

    /**
     * Identifies the effect kind used by EffectManager.
     *
     * @return the effect type
     */
    public abstract EffectType type();
    /**
     * Reads moves until this effect expires.
     *
     * @return remaining moves
     */
    public int remaining() { return remaining; }
    /** Consumes one move of this effect's duration. */
    void tick() { remaining--; }
}
