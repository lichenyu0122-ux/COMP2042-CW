package com.comp2042.model;

public final class SlowEffect extends TimedEffect {
    public static final double SPEED_MULTIPLIER = 0.5;
    public SlowEffect() { super(8); }
    @Override public EffectType type() { return EffectType.SLOW; }
}
