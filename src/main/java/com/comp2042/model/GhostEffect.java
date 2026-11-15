package com.comp2042.model;

public final class GhostEffect extends TimedEffect {
    public GhostEffect() { super(20); }
    @Override public EffectType type() { return EffectType.GHOST; }
}
