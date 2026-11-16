package com.comp2042.model;

import java.util.EnumMap;
import java.util.Map;

/**
 * Refreshes an effect of the same type while allowing different effect types to coexist.
 */
public final class EffectManager {
    /** Creates an empty collection of active effects. */
    public EffectManager() {
    }
    private final Map<EffectType, TimedEffect> active = new EnumMap<>(EffectType.class);

    /**
     * Activates an effect, replacing any existing effect of the same type.
     *
     * @param effect the effect instance to activate
     */
    public void activate(TimedEffect effect) { active.put(effect.type(), effect); }
    /**
     * Checks whether an effect type is active.
     *
     * @param type the effect kind
     * @return true if that effect has not expired
     */
    public boolean isActive(EffectType type) { return active.containsKey(type); }
    /**
     * How many moves an effect has left; returns zero if it is not active.
     *
     * @param type the effect kind
     * @return remaining moves, or zero when inactive
     */
    public int remaining(EffectType type) {
        TimedEffect effect = active.get(type);
        return effect == null ? 0 : effect.remaining();
    }
    /**
     * Removes an active shield after it absorbs a lethal move.
     *
     * @return true if a shield was consumed
     */
    public boolean consumeShield() { return active.remove(EffectType.SHIELD) != null; }
    /**
     * Calculates the movement-rate multiplier for the Slow effect.
     *
     * @return the Slow multiplier if active, otherwise 1.0
     */
    public double speedMultiplier() { return isActive(EffectType.SLOW) ? SlowEffect.SPEED_MULTIPLIER : 1.0; }
    /**
     * Decrements all active durations by one move and removes expired effects.
     */
    public void tick() {
        active.values().forEach(TimedEffect::tick);
        active.values().removeIf(effect -> effect.remaining() == 0);
    }
    /**
     * Removes all active effects on restart.
     */
    public void clear() { active.clear(); }
}
