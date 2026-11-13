package com.comp2042.model;

public final class ShieldEffect extends TimedEffect {
    public ShieldEffect() { super(20); }
    @Override public EffectType type() { return EffectType.SHIELD; }
}
