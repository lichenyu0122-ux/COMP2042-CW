package com.comp2042.model;

/**
 * Reduces movement rate for a limited number of moves.
 */
public final class SlowEffect extends TimedEffect {
    /**
     * Movement rate multiplier while Slow is active.
     */
    public static final double SPEED_MULTIPLIER = 0.5;
    /**
     * Creates a new Slow effect with its initial move duration.
     */
    public SlowEffect() { super(8); }
    /**
     * Identifies this concrete effect.
     *
     * @return the SLOW effect type
     */
    @Override public EffectType type() { return EffectType.SLOW; }
}
