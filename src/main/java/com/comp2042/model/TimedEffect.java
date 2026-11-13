package com.comp2042.model;

/** A power-up measured in game moves rather than wall-clock time. */
public abstract class TimedEffect {
    private int remaining;

    protected TimedEffect(int duration) {
        if (duration <= 0) throw new IllegalArgumentException("Duration must be positive");
        remaining = duration;
    }

    public abstract EffectType type();
    public int remaining() { return remaining; }
    void tick() { remaining--; }
}
