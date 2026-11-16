package com.comp2042.model;

/**
 * Absorbs one lethal move before its duration expires.
 */
public final class ShieldEffect extends TimedEffect {
    /**
     * Creates a new Shield effect with its initial move duration.
     */
    public ShieldEffect() { super(20); }
    /**
     * Identifies this concrete effect.
     *
     * @return the SHIELD effect type
     */
    @Override public EffectType type() { return EffectType.SHIELD; }
}
