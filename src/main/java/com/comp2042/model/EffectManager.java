package com.comp2042.model;

import java.util.EnumMap;
import java.util.Map;

/** Refreshes same-type effects and allows different types to coexist. */
public final class EffectManager {
    private final Map<EffectType, TimedEffect> active = new EnumMap<>(EffectType.class);

    public void activate(TimedEffect effect) { active.put(effect.type(), effect); }
    public boolean isActive(EffectType type) { return active.containsKey(type); }
    public int remaining(EffectType type) {
        TimedEffect effect = active.get(type);
        return effect == null ? 0 : effect.remaining();
    }
    public boolean consumeShield() { return active.remove(EffectType.SHIELD) != null; }
    public double speedMultiplier() { return isActive(EffectType.SLOW) ? SlowEffect.SPEED_MULTIPLIER : 1.0; }
    public void tick() {
        active.values().forEach(TimedEffect::tick);
        active.values().removeIf(effect -> effect.remaining() == 0);
    }
    public void clear() { active.clear(); }
}
