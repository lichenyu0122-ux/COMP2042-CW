package com.comp2042.model;

/**
 * Lets the snake cross board edges and preset walls while active.
 */
public final class GhostEffect extends TimedEffect {
    /**
     * Creates a new Ghost effect with its initial move duration.
     */
    public GhostEffect() { super(20); }
    /**
     * Identifies this concrete effect.
     *
     * @return the GHOST effect type
     */
    @Override public EffectType type() { return EffectType.GHOST; }
}
