package com.comp2042.model;

public final class PowerUpFactory {
    private PowerUpFactory() {}
    public static TimedEffect create(EffectType type) {
        return switch (type) {
            case SHIELD -> new ShieldEffect();
            case SLOW -> new SlowEffect();
            case GHOST -> new GhostEffect();
        };
    }
}
