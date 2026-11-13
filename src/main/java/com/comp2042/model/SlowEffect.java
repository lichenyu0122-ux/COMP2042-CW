package com.comp2042.model;

public final class SlowEffect extends TimedEffect {
    public SlowEffect() { super(8); }
    @Override public EffectType type() { return EffectType.SLOW; }
}
