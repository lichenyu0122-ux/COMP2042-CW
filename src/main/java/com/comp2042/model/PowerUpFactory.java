package com.comp2042.model;

/**
 * Creates a fresh timed effect for a requested effect type.
 */
public final class PowerUpFactory {
    private PowerUpFactory() {}
    /**
     * Builds a fresh effect instance for the requested type.
     *
     * @param type the effect kind
     * @return a new timed effect of that type
     * @throws NullPointerException if type is null
     */
    public static TimedEffect create(EffectType type) {
        return switch (type) {
            case SHIELD -> new ShieldEffect();
            case SLOW -> new SlowEffect();
            case GHOST -> new GhostEffect();
        };
    }
}
